package ostasp.bookapp.order.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase;
import ostasp.bookapp.order.db.OrderJpaRepository;
import ostasp.bookapp.order.domain.Order;
import ostasp.bookapp.order.domain.OrderStatus;

import java.util.List;

@Service
@AllArgsConstructor
class ManipulateOrderService implements ManipulateOrderUseCase {

    private final OrderJpaRepository repository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {

        Order order = Order.builder()
                .recipient(command.getRecipient())
                .items(command.getItems())
                .build();
        Order save = repository.save(order);
        return PlaceOrderResponse.SUCCESS(save.getId());
    }

    @Override
    public void deleteOrderById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public UpdateOrderStatusResponse updateOrderStatus(Long id, OrderStatus status) {
        return repository
                .findById(id)
                .map(order ->{
                    order.setStatus(status);
                    repository.save(order);
                    return UpdateOrderStatusResponse.SUCCESS;
                })
                .orElseGet(() -> new UpdateOrderStatusResponse(false, List.of("Order not found with id: " + id)));
    }


}
