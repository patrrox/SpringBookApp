package ostasp.bookapp.order.price.discountStrategy;

import ostasp.bookapp.order.domain.Order;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculate (Order order);
}
