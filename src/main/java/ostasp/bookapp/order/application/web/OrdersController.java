package ostasp.bookapp.order.application.web;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase.PlaceOrderResponse;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase.UpdateOrderStatusResponse;
import ostasp.bookapp.order.application.port.QueryOrderUseCase;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import ostasp.bookapp.order.application.port.QueryOrderUseCase.*;
import ostasp.bookapp.order.domain.OrderItem;
import ostasp.bookapp.order.domain.OrderStatus;
import ostasp.bookapp.order.domain.Recipient;


import static org.springframework.http.HttpStatus.*;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrdersController {

    private final ManipulateOrderUseCase manipulateOrder;
    private final QueryOrderUseCase queryOrder;


    @GetMapping
    public List<RichOrder> getOrders() {
        return queryOrder.findAll();
    }

    @GetMapping("/{id}")
    ResponseEntity<RichOrder> getOrderById(@PathVariable Long id) {
        return queryOrder
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<?> createOrder(@RequestBody PlaceOrderCommand command) {
        PlaceOrderResponse placeOrderResponse = manipulateOrder.placeOrder(command);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/" + placeOrderResponse.getOrderId().toString()).build().toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(ACCEPTED)
    public void updateOrder(@PathVariable Long id, @RequestBody UpdateStatusCommand command) {
        OrderStatus orderStatus = OrderStatus
                .parseString(command.status)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Unknown status: " + command.status));
        UpdateOrderStatusResponse response = manipulateOrder.updateOrderStatus(id, orderStatus);

        if (!response.isSuccess()) {
            String message = String.join(", ", response.getErrors());
            throw new ResponseStatusException(BAD_REQUEST, message);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manipulateOrder.deleteOrderById(id);
    }


    @Data
    static class UpdateStatusCommand {
        String status;
    }


}
