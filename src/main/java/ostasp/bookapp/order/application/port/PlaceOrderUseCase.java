package ostasp.bookapp.order.application.port;

import lombok.Builder;
import lombok.Value;
import ostasp.bookapp.order.domain.OrderItem;
import ostasp.bookapp.order.domain.Recipient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface PlaceOrderUseCase {

    PlaceOrderResponse placeOrder(PlaceOrderCommand command);

    @Builder
    @Value
    class PlaceOrderCommand {
        List<OrderItem> items;
        Recipient recipient;
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
}
