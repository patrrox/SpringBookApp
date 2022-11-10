package ostasp.bookapp.order.price.discountStrategy;

import ostasp.bookapp.order.domain.Order;
import ostasp.bookapp.order.domain.OrderItem;
import ostasp.bookapp.order.price.discountStrategy.DiscountStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public class TotalPriceDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(Order order) {
        if (isGreaterOrEqual(order,400)) {
            return lowestBookPrice(order.getItems());
        }
        if (isGreaterOrEqual(order,200)) {
            //chepest book half price
            BigDecimal lowestPrice = lowestBookPrice(order.getItems());
            return lowestPrice.divide(BigDecimal.TWO, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;

    }

    private BigDecimal lowestBookPrice( Set<OrderItem> orderItems){
        return orderItems.stream()
                .map(x -> x.getBook().getPrice())
                .sorted()
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    private static boolean isGreaterOrEqual(Order order, int value) {
        return order.getItemsPrice().compareTo(BigDecimal.valueOf(value)) >= 0;
    }
}
