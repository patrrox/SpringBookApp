package ostasp.bookapp.order.application.port;

import lombok.Value;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.order.domain.Order;
import ostasp.bookapp.order.domain.OrderItem;
import ostasp.bookapp.order.domain.OrderStatus;
import ostasp.bookapp.order.domain.Recipient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QueryOrderUseCase {

    List<RichOrder> findAll();

    Optional<RichOrder> findById(Long id);

    @Value
    class RichOrder {
        Long id;
        OrderStatus status;
        Set<OrderItem> items;
        Recipient recipient;
        LocalDateTime createdAt;

        public BigDecimal totalPrice() {
            return items.stream()
                    .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);


        }
    }
}
