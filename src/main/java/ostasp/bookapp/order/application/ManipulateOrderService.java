package ostasp.bookapp.order.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ostasp.bookapp.catalog.db.BookJpaRepository;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase;
import ostasp.bookapp.order.db.OrderJpaRepository;
import ostasp.bookapp.order.domain.Order;
import ostasp.bookapp.order.domain.OrderItem;
import ostasp.bookapp.order.domain.OrderStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
class ManipulateOrderService implements ManipulateOrderUseCase {

    private final OrderJpaRepository repository;
    private final BookJpaRepository bookRepository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {

        Set<OrderItem> items = command.getItems()
                .stream()
                .map(this::toOrderItem)
                .collect(Collectors.toSet());

        Order order = Order.builder()
                .recipient(command.getRecipient())
                .items(items)
                .build();
        Order save = repository.save(order);
        bookRepository.saveAll(updateBooks(items));
        return PlaceOrderResponse.SUCCESS(save.getId());
    }

    private Set<Book> updateBooks(Set<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() - item.getQuantity());
                    return book;
                }).collect(Collectors.toSet());
    }

    private OrderItem toOrderItem(OrderItemCommand command) {
        Book book = bookRepository
                .findById(command.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id:" + command.getBookId()));

        if (book.getAvailable() >= command.getQuantity()) {
            return new OrderItem(book, command.getQuantity());
        }
        throw new IllegalArgumentException("Too many copies of book " + book.getId() + " requested: " + command.getQuantity() + " of " + book.getAvailable() + " available");
    }

    @Override
    public void deleteOrderById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public UpdateOrderStatusResponse updateOrderStatus(Long id, OrderStatus status) {
        return repository
                .findById(id)
                .map(order -> {
                    order.updateStatus(status);
                    repository.save(order);
                    return UpdateOrderStatusResponse.SUCCESS;
                })
                .orElseGet(() -> new UpdateOrderStatusResponse(false, List.of("Order not found with id: " + id)));
    }


}
