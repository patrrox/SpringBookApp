package ostasp.bookaro.order.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ostasp.bookaro.order.application.port.PlaceOrderUseCase;
import ostasp.bookaro.order.domain.Order;
import ostasp.bookaro.order.domain.OrderRepository;

@Service
@AllArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {

    private final OrderRepository repository;

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
