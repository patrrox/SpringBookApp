package ostasp.bookapp.order.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ostasp.bookapp.catalog.db.BookJpaRepository;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase;
import ostasp.bookapp.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ostasp.bookapp.order.application.port.ManipulateOrderUseCase.*;


@DataJpaTest
@Import({ManipulateOrderService.class})
@DirtiesContext
@AutoConfigureTestDatabase
class ManipulateOrderServiceTest {

    @Autowired
    BookJpaRepository bookRepository;

    @Autowired
    ManipulateOrderUseCase service;

    @Test
    public void userCanPlaceOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        Book jcip = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .items(List.of(new OrderItemCommand(effectiveJava.getId(), 15), new OrderItemCommand(jcip.getId(), 10)))
                .build();
        //when
        PlaceOrderResponse response = service.placeOrder(command);
        //then
        assertTrue(response.isSuccess());
    }

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

    private Book givenJavaConcurrency(long available) {
        return bookRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"), available));
    }

    private Book givenEffectiveJava(long available) {
        return bookRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }

    private Recipient recipient() {
        return recipient("john@example.org");
    }

    private Recipient recipient(String email) {
        return Recipient.builder().email(email).build();
    }


}