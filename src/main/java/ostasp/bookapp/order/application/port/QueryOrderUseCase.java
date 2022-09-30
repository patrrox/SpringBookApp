package ostasp.bookapp.order.application.port;

import ostasp.bookapp.order.domain.Order;

import java.util.List;

public interface QueryOrderUseCase {

    List<Order> findAll();

}
