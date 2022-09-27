package ostasp.bookaro.catalog.infrastructure;

import org.springframework.stereotype.Repository;
import ostasp.bookaro.catalog.domain.Book;
import ostasp.bookaro.catalog.domain.CatalogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryCatalogRepositoryImpl implements CatalogRepository {

    private final Map<Long,Book> storage = new ConcurrentHashMap<>();

    public MemoryCatalogRepositoryImpl() {
        storage.put(1L, new Book(1L,"Pan Tadeusz", "Adam Mickiewicz", 1834));
        storage.put(2L, new Book(1L,"Ogniem i Mieczem", "Henryk Sienkiewicz", 1884));
        storage.put(3L, new Book(1L,"Chłopi", "Władysaw Reymont", 1884));
        storage.put(4L, new Book(1L,"Pan Wołodyjowski", "Henryk Sienkiewicz", 1884));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }
}
