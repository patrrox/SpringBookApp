package ostasp.bookapp.order.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ostasp.bookapp.order.application.port.PlaceOrderUseCase;
import ostasp.bookapp.order.db.OrderJpaRepository;
import ostasp.bookapp.order.domain.Order;

@Service
@AllArgsConstructor
class PlaceOrderService implements PlaceOrderUseCase {

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


}
