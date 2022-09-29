package ostasp.bookaro.order.domain;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class Order {

     Long id;
     OrderStatus status;
     List<OrderItem> items;
     Recipient recipient;
     LocalDateTime createdAt;

    BigDecimal totalPrice() {
        return items.stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }


}
