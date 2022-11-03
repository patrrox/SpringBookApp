package ostasp.bookapp.order.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ostasp.bookapp.order.domain.Order;

public interface OrderJpaRepository extends JpaRepository<Order,Long> {
}
