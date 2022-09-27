package ostasp.bookaro.catalog.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ostasp.bookaro.catalog.domain.Book;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService {
    private final CatalogRepository repository;

    public List<Book> findByTitle(String title) {
        return repository.findAll().stream().filter(book -> book.getTitle().startsWith(title)).collect(Collectors.toList());
    }


}
