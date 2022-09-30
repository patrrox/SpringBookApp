package ostasp.bookaro.order.infrastructure;

import org.springframework.stereotype.Repository;
import ostasp.bookaro.order.domain.Order;
import ostasp.bookaro.order.domain.OrderRepository;

import java.sql.Array;
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
            return storage.put(order.getId(), order);
        } else {
            long nextId = nextId();
            order.setId(nextId);
            order.setCreatedAt(LocalDateTime.now());
            return storage.put(nextId, order);
        }
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    private long nextId() {
        return ID_NEXT_VALUE.getAndIncrement();
    }
}
