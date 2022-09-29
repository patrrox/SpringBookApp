package ostasp.bookaro;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ostasp.bookaro.catalog.application.port.CatalogUseCase;
import ostasp.bookaro.catalog.application.port.CatalogUseCase.*;
import ostasp.bookaro.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalog;
    private final String title;
    private final Long limit;
    private final String author;

    public ApplicationStartup(CatalogUseCase catalog,
                              @Value("${bookaro.catalog.query}") String title,
                              @Value("${bookaro.catalog.limit:3}") Long limit,
                              @Value("${bookaro.catalog.author}") String author) {
        this.catalog = catalog;
        this.title = title;
        this.limit = limit;
        this.author = author;
    }

    @Override
    public void run(String... args) throws Exception {
        initData();
        findByTitle();
        findAndUpdate();
        findByTitle();

    }

    private void findByTitle() {
        List<Book> booksByTitle = catalog.findByTitle(title);
        booksByTitle.forEach(System.out::println);
    }


    private void initData() {
        catalog.addBook(new CreateBookCommand("Pan Tadeusz", "Adam Mickiewicz", 1834, BigDecimal.valueOf(25)));
        catalog.addBook(new CreateBookCommand("Ogniem i Mieczem", "Henryk Sienkiewicz", 1884,BigDecimal.valueOf(25)));
        catalog.addBook(new CreateBookCommand("Chłopi", "Władysaw Reymont", 1884,BigDecimal.valueOf(25)));
        catalog.addBook(new CreateBookCommand("Pan Wołodyjowski", "Henryk Sienkiewicz", 1884,BigDecimal.valueOf(25)));
    }

    private void findAndUpdate() {

        System.out.println("Updating book ...");
        catalog.findOneByTitleAndAuthor("Pan Tadeusz", "Adam Mickiewicz")
                .ifPresent(book -> {

                    UpdateBookCommand command = UpdateBookCommand
                            .builder()
                            .id(book.getId())
                            .title("Pan Tadeusz, czyli ostatni zajazd na Litwie")
                            .build();
                    UpdateBookResponse response = catalog.updateBook(command);
                    System.out.println("Updating book result:" + response.isSuccess());
                });
    }
}
