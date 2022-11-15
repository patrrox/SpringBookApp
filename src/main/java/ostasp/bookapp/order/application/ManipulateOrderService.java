package ostasp.bookapp.order.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ostasp.bookapp.security.UserSecurity;
import ostasp.bookapp.catalog.db.BookJpaRepository;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase;
import ostasp.bookapp.order.db.OrderJpaRepository;
import ostasp.bookapp.order.db.RecipientJpaRepository;
import ostasp.bookapp.order.domain.*;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
class ManipulateOrderService implements ManipulateOrderUseCase {

    private final OrderJpaRepository repository;
    private final BookJpaRepository bookRepository;
    private final RecipientJpaRepository recipientRepository;
    private final UserSecurity userSecurity;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {

        Set<OrderItem> items = command.getItems()
                .stream()
                .map(this::toOrderItem)
                .collect(Collectors.toSet());
        Order order = Order.builder()
                .recipient(getOrCreateRecipient(command.getRecipient()))
                .delivery(command.getDelivery())
                .items(items)
                .build();
        Order save = repository.save(order);
        bookRepository.saveAll(reduceBooks(items));
        return PlaceOrderResponse.SUCCESS(save.getId());
    }

    private Recipient getOrCreateRecipient(Recipient recipient) {
        return recipientRepository
                .findByEmailIgnoreCase(recipient.getEmail())
                .orElse(recipient);
    }

    private Set<Book> reduceBooks(Set<OrderItem> items) {
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

        if (command.getQuantity() <= 0)
            throw new IllegalArgumentException("Quantity cannot be negative : [bookId: "
                    + command.getBookId() + " quantity: " + command.getQuantity() + "]");
        if (book.getAvailable() >= command.getQuantity()) {
            return new OrderItem(book, command.getQuantity());
        } else {
            throw new IllegalArgumentException("Too many copies of book " + book.getId() + " requested: " + command.getQuantity() + " of " + book.getAvailable() + " available");
        }

    }

    @Override
    public void deleteOrderById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public UpdateOrderStatusResponse updateOrderStatus(UpdateStatusCommand command) {
        return repository
                .findById(command.getOrderId())
                .map(order -> {
                    if (userSecurity.isOwnerOrAdmin(order.getRecipient().getEmail(), command.getUser())){
                        UpdateStatusResult result = order.updateStatus(command.getStatus());
                        if (result.isRevoked()) {
                            bookRepository.saveAll(revokeBooks(order.getItems()));
                        }
                        repository.save(order);
                        return UpdateOrderStatusResponse.SUCCESS;
                    }
                    return UpdateOrderStatusResponse.FAILURE(Error.FORBIDDEN);
                    //else
                })
                .orElseGet(() -> new UpdateOrderStatusResponse(false,Error.NOT_FOUND));
    }



    private Set<Book> revokeBooks(Set<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() + item.getQuantity());
                    return book;
                }).collect(Collectors.toSet());
    }


}
