package ostasp.bookapp.order.application.port;

import lombok.*;
import ostasp.bookapp.order.domain.OrderStatus;
import ostasp.bookapp.order.domain.Recipient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface ManipulateOrderUseCase {

    PlaceOrderResponse placeOrder(PlaceOrderCommand command);

    void deleteOrderById(Long id);

    UpdateOrderStatusResponse updateOrderStatus(Long id, OrderStatus status);

    @Builder
    @Value
    @AllArgsConstructor
    class PlaceOrderCommand {
        List<OrderItemCommand> items;
        Recipient recipient;
    }

    @Value
    class OrderItemCommand {
        Long bookId;
        int quantity;
    }


    @Value
    class PlaceOrderResponse {
        boolean success;
        Long orderId;
        List<String> errors;

        public static PlaceOrderResponse SUCCESS(Long orderId) {
            return new PlaceOrderResponse(true, orderId, Collections.emptyList());
        }

        public static PlaceOrderResponse FAILURE(String... errors) {
            return new PlaceOrderResponse(true, null, Arrays.asList(errors));
        }
    }

    @Value
    @AllArgsConstructor
    class UpdateOrderStatusResponse {
        boolean success;
        List<String> errors;
        public static final UpdateOrderStatusResponse SUCCESS = new UpdateOrderStatusResponse(true, Collections.emptyList());
    }
}
