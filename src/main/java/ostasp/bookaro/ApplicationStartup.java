package ostasp.bookaro;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ostasp.bookaro.catalog.application.CatalogController;
import ostasp.bookaro.catalog.domain.Book;

import java.util.List;

@Component
@AllArgsConstructor
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogController catalogController;

    @Override
    public void run(String... args) throws Exception {
        List<Book> books = catalogController.findByTitle("Pan");
        System.out.println(books);
    }
}
