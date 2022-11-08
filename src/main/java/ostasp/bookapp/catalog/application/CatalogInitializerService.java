package ostasp.bookapp.catalog.application;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ostasp.bookapp.catalog.application.port.CatalogInitializerUseCase;
import ostasp.bookapp.catalog.application.port.CatalogUseCase;
import ostasp.bookapp.catalog.application.port.CatalogUseCase.CreateBookCommand;
import ostasp.bookapp.catalog.db.AuthorJpaRepository;
import ostasp.bookapp.catalog.domain.Author;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.jpa.BaseEntity;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase;
import ostasp.bookapp.order.application.port.QueryOrderUseCase;
import ostasp.bookapp.order.domain.Recipient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CatalogInitializerService implements CatalogInitializerUseCase {

    private final CatalogUseCase catalog;
    private final ManipulateOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final AuthorJpaRepository authorRepository;

    @Override
    @Transactional
    public void initialize() {
        initData();
        placeOrder();
    }

    private void initData() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("books.csv").getInputStream()))) {
            CsvToBean<CsvBook> build = new CsvToBeanBuilder<CsvBook>(reader)
                    .withType(CsvBook.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            build.stream().forEach(this::initBook);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse CSV file", e);
        }
    }

    private void initBook(CsvBook csvBook) {
        //parse authors


        Set<Long> authors = Arrays.stream(csvBook.authors.split(","))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .map(this::getOrCreateAuthor)
                .map(BaseEntity::getId)
                .collect(Collectors.toSet());


        CreateBookCommand command = new CreateBookCommand(csvBook.title, authors, csvBook.year, csvBook.amount, 50L);
        catalog.addBook(command);
        //upload thumbnail
    }

    private Author getOrCreateAuthor(String name) {
        return authorRepository
                .findByNameIgnoreCase(name)
                .orElseGet(() -> authorRepository.save(new Author(name)));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CsvBook {
        @CsvBindByName
        private String title;
        @CsvBindByName
        private String authors;
        @CsvBindByName
        private Integer year;
        @CsvBindByName
        private BigDecimal amount;
        @CsvBindByName
        private String thumbnail;
    }

    private void placeOrder() {
        Book effectiveJava = catalog.findOneByTitle("Effective Java").orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        Book puzzlers = catalog.findOneByTitle("Java Puzzlers").orElseThrow(() -> new IllegalStateException("Cannot find a book"));

        //create recipent

        Recipient recipient = Recipient
                .builder()
                .name("Jan Kowalski")
                .phone("123 456 789")
                .street("Armi Krajowej 123")
                .city("Wrocław")
                .zipCode("51-555")
                .email("jan@kowalski.gmail.com")
                .build();

        //place order command
        ManipulateOrderUseCase.PlaceOrderCommand placeOrderCommand = ManipulateOrderUseCase.PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .items(Arrays.asList(new ManipulateOrderUseCase.OrderItemCommand(effectiveJava.getId(), 16), new ManipulateOrderUseCase.OrderItemCommand(puzzlers.getId(), 7)))
                .build();

        ManipulateOrderUseCase.PlaceOrderResponse response = placeOrder.placeOrder(placeOrderCommand);
        log.info("Created ORDER with id: " + response.getOrderId());


        //list of all orders
        queryOrder.findAll()
                .forEach(order -> log.info("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS: " + order));

    }

//    private void initData() {
//
//        Author joshua = new Author("Joshua", "Bloch");
//        Author neal = new Author("Neal", "Gafter");
//        authorRepository.save(joshua);
//        authorRepository.save(neal);
//
//        CatalogUseCase.CreateBookCommand effectiveJava = new CatalogUseCase.CreateBookCommand(
//                "Effective Java",
//                Set.of(joshua.getId()),
//                2005,
//                new BigDecimal("79.00"),
//                50L
//        );
//
//        CatalogUseCase.CreateBookCommand javaPuzzlers = new CatalogUseCase.CreateBookCommand(
//                "Java Puzzlers",
//                Set.of(joshua.getId(), neal.getId()),
//                2018,
//                new BigDecimal("99.00"),
//                50L
//        );
//
//
//        catalog.addBook(effectiveJava);
//        catalog.addBook(javaPuzzlers);
//    }
}
