package ostasp.bookapp.order.domain;

import java.util.List;

public interface OrderRepository {

    Order save(Order order);

    List<Order> findAll();
}
