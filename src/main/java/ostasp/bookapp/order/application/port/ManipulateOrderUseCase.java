package ostasp.bookapp.order.application.port;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import ostasp.bookapp.order.domain.Delivery;
import ostasp.bookapp.order.domain.OrderStatus;
import ostasp.bookapp.order.domain.Recipient;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface ManipulateOrderUseCase {

    PlaceOrderResponse placeOrder(PlaceOrderCommand command);

    void deleteOrderById(Long id);

    UpdateOrderStatusResponse updateOrderStatus(UpdateStatusCommand command);

    @Builder
    @Value
    @AllArgsConstructor
    class PlaceOrderCommand {
        List<OrderItemCommand> items;
        Recipient recipient;
        @Builder.Default
        @Enumerated(EnumType.STRING)
        Delivery delivery =Delivery.COURIER;

    }

    @Value
    class OrderItemCommand {
        Long bookId;
        int quantity;
    }

    @Value
    class UpdateStatusCommand{
        Long orderId;
        OrderStatus status;
        UserDetails user;
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
        Error error;
        public static final UpdateOrderStatusResponse SUCCESS = new UpdateOrderStatusResponse(true, null);

        public static UpdateOrderStatusResponse FAILURE(Error error) {
            return new UpdateOrderStatusResponse(false, error);
        }
    }

    @Getter
    @AllArgsConstructor
    enum Error {
        NOT_FOUND (HttpStatus.NOT_FOUND),
        FORBIDDEN(HttpStatus.FORBIDDEN);

        private final HttpStatus status;
    }
}
