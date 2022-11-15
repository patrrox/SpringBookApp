package ostasp.bookapp.order.application;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import ostasp.bookapp.catalog.application.port.CatalogUseCase;
import ostasp.bookapp.catalog.db.BookJpaRepository;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.clock.Clock;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase;
import ostasp.bookapp.order.application.port.QueryOrderUseCase;
import ostasp.bookapp.order.domain.OrderStatus;
import ostasp.bookapp.order.domain.Recipient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        properties = "app.orders.payment-period: 1H"
)
@AutoConfigureTestDatabase
class AbandonedOrdersJobTest {

    @TestConfiguration
    static class TestConfig{
    @Bean
        public Clock.MockTestClock clock(){
        return new Clock.MockTestClock();
    }
    }

    @Autowired
    AbandonedOrdersJob ordersJob;

    @Autowired
    Clock.MockTestClock clock;

    @Autowired
    QueryOrderUseCase queryOrderService;

    @Autowired
    BookJpaRepository bookRepository;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    ManipulateOrderUseCase manipulateOrderService;

    @Test
    public void shouldMarkOrdersAsAbandoned() {

        //given - orders
        Book book = givenEffectiveJava(50L);
        Long orderId = placedOrder(book.getId(), 15);

        //when - run
        clock.tick(Duration.ofHours(2));
        ordersJob.run();
        //then - status changed
        assertEquals(OrderStatus.ABANDONED, queryOrderService.findById(orderId).get().getStatus());
        assertEquals(50L, getAvailableCopiesFromBook(book));

    }

    private Long getAvailableCopiesFromBook(Book book) {
        return catalogUseCase
                .findById(book.getId())
                .get()
                .getAvailable();
    }

    private Long placedOrder(Long bookId, int quantity) {
        ManipulateOrderUseCase.PlaceOrderCommand command = ManipulateOrderUseCase.PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .items(List.of(
                        new ManipulateOrderUseCase.OrderItemCommand(bookId, quantity)))
                .build();
        return manipulateOrderService.placeOrder(command).getOrderId();
    }


    private Book givenEffectiveJava(long available) {
        return bookRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }


    private Recipient recipient() {
        return Recipient.builder().email("john@example.org").build();
    }

}