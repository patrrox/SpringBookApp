package ostasp.bookapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ostasp.bookapp.catalog.application.port.CatalogUseCase;
import ostasp.bookapp.catalog.application.port.CatalogUseCase.*;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.order.application.port.PlaceOrderUseCase;
import ostasp.bookapp.order.application.port.PlaceOrderUseCase.*;
import ostasp.bookapp.order.application.port.QueryOrderUseCase;
import ostasp.bookapp.order.domain.OrderItem;
import ostasp.bookapp.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalog;
    private final PlaceOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final String title;
    private final Long limit;
    private final String author;

    public ApplicationStartup(CatalogUseCase catalog,
                              PlaceOrderUseCase placeOrder,
                              QueryOrderUseCase queryOrder,
                              @Value("${bookaro.catalog.query}") String title,
                              @Value("${bookaro.catalog.limit:3}") Long limit,
                              @Value("${bookaro.catalog.author}") String author) {
        this.catalog = catalog;
        this.placeOrder = placeOrder;
        this.queryOrder = queryOrder;
        this.title = title;
        this.limit = limit;
        this.author = author;
    }

    @Override
    public void run(String... args) throws Exception {
        initData();
        searchCatalog();
        placeOrder();
    }

    private void placeOrder() {
        Book panTadeusz = catalog.findOneByTitle("Pan Tadeusz").orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        Book chlopi = catalog.findOneByTitle("Chłopi").orElseThrow(() -> new IllegalStateException("Cannot find a book"));

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
        PlaceOrderCommand placeOrderCommand = PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .items(Arrays.asList(new OrderItem(panTadeusz.getId(), 16), new OrderItem(chlopi.getId(), 7)))
                .build();

        PlaceOrderResponse response = placeOrder.placeOrder(placeOrderCommand);
        System.out.println("Created ORDER with id: " + response.getOrderId());


        //list of all orders
        queryOrder.findAll()
                .forEach(order -> System.out.println("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS: " + order));

    }

    private void searchCatalog() {
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
        catalog.addBook(new CreateBookCommand("Ogniem i Mieczem", "Henryk Sienkiewicz", 1884, BigDecimal.valueOf(25)));
        catalog.addBook(new CreateBookCommand("Chłopi", "Władysaw Reymont", 1884, BigDecimal.valueOf(25)));
        catalog.addBook(new CreateBookCommand("Pan Wołodyjowski", "Henryk Sienkiewicz", 1884, BigDecimal.valueOf(25)));
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
