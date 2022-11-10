package ostasp.bookapp.order.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ostasp.bookapp.clock.Clock;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase;
import ostasp.bookapp.order.db.OrderJpaRepository;
import ostasp.bookapp.order.domain.Order;
import ostasp.bookapp.order.domain.OrderStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static ostasp.bookapp.order.application.port.ManipulateOrderUseCase.*;

@Slf4j
@Component
@AllArgsConstructor
public class AbandonedOrdersJob {
    private final OrderJpaRepository repository;
    private final ManipulateOrderUseCase orderUseCase;
    private final OrdersProperties properties;
    private final Clock clock;

    @Scheduled(cron = "${app.orders.abandon-cron}")
    @Transactional
    public void run() {
        Duration paymentPeriod = properties.getPaymentPeriod();
        LocalDateTime olderThan = clock.now().minus(paymentPeriod);

        //test
        List<Order> all = repository.findAll();

        List<Order> orders = repository.findByStatusAndCreatedAtLessThanEqual(OrderStatus.NEW, olderThan);
        log.info("Found orders to be abandoned: " + orders.size());
        orders.forEach(order -> {
            //TODO: SECURITY EMAIL
            String adminEmail = "admin@example.org";
            UpdateStatusCommand command = new UpdateStatusCommand(order.getId(), OrderStatus.ABANDONED, adminEmail);
            orderUseCase.updateOrderStatus(command);
        });
    }
}
