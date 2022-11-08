package ostasp.bookapp.order.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ostasp.bookapp.catalog.db.BookJpaRepository;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.order.application.port.QueryOrderUseCase;
import ostasp.bookapp.order.db.OrderJpaRepository;
import ostasp.bookapp.order.domain.Order;
import ostasp.bookapp.order.domain.OrderItem;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class QueryOrderService implements QueryOrderUseCase {

    private final OrderJpaRepository repository;
    private final BookJpaRepository catalogRepository;

    @Override
    @Transactional
    public List<RichOrder> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toRichOrder)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RichOrder> findById(Long id) {
        return repository.findById(id).map(this::toRichOrder);
    }

    private RichOrder toRichOrder(Order order) {
        return new RichOrder(
                order.getId(),
                order.getStatus(),
                order.getItems(),
                order.getRecipient(),
                order.getCreatedAt()
        );
    }


}
