package ostasp.bookaro.catalog.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import ostasp.bookaro.catalog.domain.Book;
import ostasp.bookaro.catalog.domain.CatalogService;

import java.util.List;
@Controller
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService service;

    public List<Book> findByTitle(String title){
        return service.findByTitle(title);
    }
}
