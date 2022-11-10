package ostasp.bookapp.order.price;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ostasp.bookapp.order.domain.Order;
import ostasp.bookapp.order.price.discountStrategy.DeliveryDiscountStrategy;
import ostasp.bookapp.order.price.discountStrategy.DiscountStrategy;
import ostasp.bookapp.order.price.discountStrategy.TotalPriceDiscountStrategy;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PriceService {

    private final List<DiscountStrategy> strategies = List.of(
            new DeliveryDiscountStrategy(),
            new TotalPriceDiscountStrategy()
    );

    @Transactional
    public OrderPrice calculatePrice(Order order) {
        return new OrderPrice(
                order.getItemsPrice(),
                order.getDeliveryPrice(),
                discounts(order));
    }

    private BigDecimal discounts(Order order) {
        return strategies.stream()
                .map(strategy -> strategy.calculate(order))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
