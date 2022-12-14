package ostasp.bookapp.order.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.annotation.DirtiesContext;
import ostasp.bookapp.catalog.application.port.CatalogUseCase;
import ostasp.bookapp.catalog.db.BookJpaRepository;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase;
import ostasp.bookapp.order.application.port.QueryOrderUseCase;
import ostasp.bookapp.order.domain.Delivery;
import ostasp.bookapp.order.domain.OrderStatus;
import ostasp.bookapp.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ostasp.bookapp.order.application.port.ManipulateOrderUseCase.*;


@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderServiceTest {

    @Autowired
    BookJpaRepository bookRepository;

    @Autowired
    ManipulateOrderUseCase service;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    QueryOrderUseCase queryOrderService;

    @Test
    public void userCantOrderMoreBooksThanAvailable() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        Book jcip = givenJavaConcurrency(5L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .items(List.of(new OrderItemCommand(effectiveJava.getId(), 15), new OrderItemCommand(jcip.getId(), 10)))
                .build();
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.placeOrder(command);
        });
        //then

        assertTrue(exception.getMessage().contains("Too many copies of book " + jcip.getId() + " requested: 10 of 5 available"));
    }

    @Test
    public void userCanPlaceOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        Book jcip = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .items(List.of(
                        new OrderItemCommand(effectiveJava.getId(), 15),
                        new OrderItemCommand(jcip.getId(), 10)))
                .build();
        //when
        PlaceOrderResponse response = service.placeOrder(command);
        //then
        assertTrue(response.isSuccess());
        assertEquals(35L, getAvailableCopiesFromBook(effectiveJava));
        assertEquals(40L, getAvailableCopiesFromBook(jcip));
    }

    @Test
    public void userCanRevokeOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, recipient);
        assertEquals(35L, getAvailableCopiesFromBook(effectiveJava));
        //when
        service.updateOrderStatus(new UpdateStatusCommand(orderId, OrderStatus.CANCELED, user(recipient)));
        //then
        assertEquals(50L, getAvailableCopiesFromBook(effectiveJava));
        assertEquals(OrderStatus.CANCELED, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    public void userCannotRevokePaidOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, recipient);
        assertEquals(OrderStatus.NEW, queryOrderService.findById(orderId).get().getStatus());
        //when
        service.updateOrderStatus(new UpdateStatusCommand(orderId, OrderStatus.PAID, user(recipient)));
        assertEquals(OrderStatus.PAID, queryOrderService.findById(orderId).get().getStatus());
        IllegalArgumentException exceptionCanceled = assertThrows(IllegalArgumentException.class, () -> {
            service.updateOrderStatus(new UpdateStatusCommand(orderId, OrderStatus.CANCELED, user(recipient)));
        });
        IllegalArgumentException exceptionAbandoned = assertThrows(IllegalArgumentException.class, () -> {
            service.updateOrderStatus(new UpdateStatusCommand(orderId, OrderStatus.ABANDONED, user(recipient)));
        });
        //then
        assertTrue(exceptionCanceled.getMessage().contains("Unable to mark PAID order as CANCELED"));
        assertTrue(exceptionAbandoned.getMessage().contains("Unable to mark PAID order as ABANDONED"));
    }

    @Test
    public void userCannotRevokeShippedOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15,recipient);
        assertEquals(OrderStatus.NEW, queryOrderService.findById(orderId).get().getStatus());
        //when
        service.updateOrderStatus(new UpdateStatusCommand(orderId, OrderStatus.PAID, user(recipient)));
        assertEquals(OrderStatus.PAID, queryOrderService.findById(orderId).get().getStatus());
        service.updateOrderStatus(new UpdateStatusCommand(orderId, OrderStatus.SHIPPED, user(recipient)));
        assertEquals(OrderStatus.SHIPPED, queryOrderService.findById(orderId).get().getStatus());
        IllegalArgumentException exceptionCanceled = assertThrows(IllegalArgumentException.class, () -> {
            service.updateOrderStatus(new UpdateStatusCommand(orderId, OrderStatus.CANCELED, user(recipient)));
        });
        IllegalArgumentException exceptionAbandoned = assertThrows(IllegalArgumentException.class, () -> {
            service.updateOrderStatus(new UpdateStatusCommand(orderId, OrderStatus.ABANDONED, user(recipient)));
        });
        //then
        assertTrue(exceptionCanceled.getMessage().contains("Unable to mark SHIPPED order as CANCELED"));
        assertTrue(exceptionAbandoned.getMessage().contains("Unable to mark SHIPPED order as ABANDONED"));
    }


    @Test
    public void userCannotOrderNoExistingBooks() {

        //given
        Long idNotExistingBook = 1L;
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .items(List.of(new OrderItemCommand(idNotExistingBook, 15)))
                .build();
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.placeOrder(command);
        });
        //then
        assertTrue(exception.getMessage().contains("Book not found with id:1"));
    }

    @Test
    public void userCannotOrderNegativeNumberOfBooks() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        Book jcip = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .items(List.of(
                        new OrderItemCommand(effectiveJava.getId(), -15),
                        new OrderItemCommand(jcip.getId(), 10)))
                .build();
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.placeOrder(command);
        });
        //then
        assertTrue(exception.getMessage().contains("Quantity cannot be negative : [bookId: " + effectiveJava.getId() + " quantity: -15]"));


    }

    @Test
    public void userCannotRevokeOtherUsersOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "adam@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, recipient);
        assertEquals(35L, getAvailableCopiesFromBook(effectiveJava));
        //when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, user("marek@example.com"));
        UpdateOrderStatusResponse response = service.updateOrderStatus(command);
        //then
        assertEquals(35L, getAvailableCopiesFromBook(effectiveJava));
        assertEquals(OrderStatus.NEW, queryOrderService.findById(orderId).get().getStatus());
        assertEquals(response.getError(), ManipulateOrderUseCase.Error.FORBIDDEN);

    }

    @Test
    //TODO: should be changed after spring security
    public void adminCanRevokeOtherUsersOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, recipient);
        assertEquals(35L, getAvailableCopiesFromBook(effectiveJava));
        //when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, adminUser());
        UpdateOrderStatusResponse response = service.updateOrderStatus(command);
        //then
        assertEquals(50L, getAvailableCopiesFromBook(effectiveJava));
        assertEquals(OrderStatus.CANCELED, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    //TODO: should be changed after spring security
    public void adminCanMarkOrderAsPaid() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, recipient);
        assertEquals(35L, getAvailableCopiesFromBook(effectiveJava));
        //when
        service.updateOrderStatus(new UpdateStatusCommand(orderId, OrderStatus.PAID, adminUser()));
        //then
        assertEquals(35L, getAvailableCopiesFromBook(effectiveJava));
        assertEquals(OrderStatus.PAID, queryOrderService.findById(orderId).get().getStatus());
    }



    @Test
    public void shippingCostsAreAddedToTotalOrderPrice() {
        // given
        Book book = givenBook(50L, "49.90");

        // when
        Long orderId = placedOrder(book.getId(), 1);

        // then
        assertEquals("59.80", orderOf(orderId).getFinalPrice().toPlainString());
    }

    @Test
    public void shippingCostsAreDiscountedOver100zlotys() {
        // given
        Book book = givenBook(50L, "49.90");

        // when
        Long orderId = placedOrder(book.getId(), 3);

        // then
        RichOrder order = orderOf(orderId);
        assertEquals("149.70", order.getFinalPrice().toPlainString());
        assertEquals("149.70", order.getOrderPrice().getItemsPrice().toPlainString());
    }

    @Test
    public void cheapestBookIsHalfPricedWhenTotalOver200zlotys() {
        // given
        Book book = givenBook(50L, "49.90");

        // when
        Long orderId = placedOrder(book.getId(), 5);

        // then
        RichOrder order = orderOf(orderId);

        assertEquals(new BigDecimal("9.90"),order.getOrderPrice().getDeliveryPrice());
        assertEquals(new BigDecimal("34.85"),order.getOrderPrice().getDiscounts());
        assertEquals("224.55", order.getFinalPrice().toPlainString());
    }

    @Test
    public void cheapestBookIsFreeWhenTotalOver400zlotys() {
        // given
        Book book = givenBook(50L, "49.90");

        // when
        Long orderId = placedOrder(book.getId(), 10);

        RichOrder order = orderOf(orderId);
        // then
        assertEquals(new BigDecimal("9.90"),order.getOrderPrice().getDeliveryPrice());
        assertEquals(new BigDecimal("59.80"),order.getOrderPrice().getDiscounts());
        assertEquals("449.10", order.getFinalPrice().toPlainString());
    }

    private Book givenBook(long available, String price) {
        return bookRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal(price), available));
    }

    private RichOrder orderOf(Long orderId) {
        return queryOrderService.findById(orderId).get();
    }



    private Long getAvailableCopiesFromBook(Book book) {
        return catalogUseCase
                .findById(book.getId())
                .get()
                .getAvailable();
    }

    private Long placedOrder(Long bookId, int quantity, String recipient) {
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient(recipient))
                .items(List.of(
                        new OrderItemCommand(bookId, quantity)))
                .delivery(Delivery.COURIER)
                .build();
        return service.placeOrder(command).getOrderId();
    }

    private Long placedOrder(Long bookId, int quantity) {
        return placedOrder(bookId, quantity, "john@example.org");
    }

    private Book givenJavaConcurrency(long available) {
        return bookRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"), available));
    }

    private Book givenEffectiveJava(long available) {
        return bookRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }

    private User user(String email){
        return new User(email,"", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private User adminUser(){
        return new User("systemUser","", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    private Recipient recipient() {
        return recipient("john@example.org");
    }

    private Recipient recipient(String email) {
        return Recipient.builder().email(email).build();
    }


}