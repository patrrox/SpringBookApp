package ostasp.bookapp.order.application.web;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ostasp.bookapp.catalog.application.security.UserSecurity;
import ostasp.bookapp.order.application.RichOrder;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase.PlaceOrderResponse;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase.UpdateOrderStatusResponse;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase.UpdateStatusCommand;
import ostasp.bookapp.order.application.port.QueryOrderUseCase;

import java.net.URI;
import java.util.List;

import ostasp.bookapp.order.domain.OrderStatus;


import static org.springframework.http.HttpStatus.*;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrdersController {

    private final ManipulateOrderUseCase manipulateOrder;
    private final QueryOrderUseCase queryOrder;
    private final UserSecurity userSecurity;


    @Secured({"ROLE_ADMIN"})
    @GetMapping
    public List<RichOrder> getOrders() {
        return queryOrder.findAll();
    }

    @Secured({"ROLE_ADMIN, ROLE_USER"})
    @GetMapping("/{id}")
    ResponseEntity<RichOrder> getOrderById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return queryOrder
                .findById(id)
                .map(order -> authorizeAdminOrUser(order,user))
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<RichOrder> authorizeAdminOrUser(RichOrder order, User user){
        if (userSecurity.isOwnerOrAdmin(order.getRecipient().getEmail(),user)){
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.status(FORBIDDEN).build();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<?> createOrder(@RequestBody PlaceOrderCommand command) {
        PlaceOrderResponse placeOrderResponse = manipulateOrder.placeOrder(command);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/" + placeOrderResponse.getOrderId().toString()).build().toUri();
        return ResponseEntity.created(uri).build();
    }

    //TODO : admin all status
    // TODO : user with order - only cancel order
    @Secured({"ROLE_ADMIN, ROLE_USER"})
    @PatchMapping ("/{id}/status")
    @ResponseStatus(ACCEPTED)
    public void updateOrderStatus(@PathVariable Long id, @RequestBody UpdateRequestStatusCommand command,  @AuthenticationPrincipal User user) {
        OrderStatus orderStatus = OrderStatus
                .parseString(command.status)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Unknown status: " + command.status));
        //TODO: SECURITY EMAIL
        String adminEmail = "admin@example.org";//TODO: remove
        UpdateStatusCommand updateStatusCommand = new UpdateStatusCommand(id,orderStatus,user);
        UpdateOrderStatusResponse response = manipulateOrder.updateOrderStatus(updateStatusCommand);

        if (!response.isSuccess()) {
            throw new ResponseStatusException(response.getError().getStatus());
        }
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manipulateOrder.deleteOrderById(id);
    }


    @Data
    static class UpdateRequestStatusCommand {
        String status;
    }


}
