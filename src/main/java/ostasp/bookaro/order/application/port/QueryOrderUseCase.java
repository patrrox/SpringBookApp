package ostasp.bookaro.order.application.port;

import ostasp.bookaro.order.domain.Order;

import java.util.List;

public interface QueryOrderUseCase {

    List<Order> findAll();

}
