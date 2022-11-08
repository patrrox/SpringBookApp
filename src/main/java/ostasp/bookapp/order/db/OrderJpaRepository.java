package ostasp.bookapp.order.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ostasp.bookapp.order.domain.Order;
import ostasp.bookapp.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order,Long> {

    List<Order> findByStatusAndCreatedAtLessThanEqual(OrderStatus status, LocalDateTime timestamp);
}
