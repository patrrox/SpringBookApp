package ostasp.bookapp.order.infrastructure;

import org.springframework.stereotype.Repository;
import ostasp.bookapp.order.domain.Order;
import ostasp.bookapp.order.domain.OrderRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryOrderRepository implements OrderRepository {

    private final Map<Long, Order> storage = new ConcurrentHashMap<>();
    private final AtomicLong ID_NEXT_VALUE = new AtomicLong(0L);

    @Override
    public Order save(Order order) {
        if (order.getId() != null) {
             storage.put(order.getId(), order);
        } else {
            long nextId = nextId();
            order.setId(nextId);
            order.setCreatedAt(LocalDateTime.now());
             storage.put(nextId, order);
        }
        return order;
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    private long nextId() {
        return ID_NEXT_VALUE.getAndIncrement();
    }
}
