package ostasp.bookapp;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ostasp.bookapp.catalog.application.port.CatalogUseCase;
import ostasp.bookapp.catalog.application.port.CatalogUseCase.*;
import ostasp.bookapp.catalog.db.AuthorJpaRepository;
import ostasp.bookapp.catalog.domain.Author;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase.*;
import ostasp.bookapp.order.application.port.QueryOrderUseCase;
import ostasp.bookapp.order.domain.OrderItem;
import ostasp.bookapp.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
@AllArgsConstructor
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalog;
    private final ManipulateOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final AuthorJpaRepository authorRepository;


    @Override
    public void run(String... args) throws Exception {
        initData();
        placeOrder();
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
        PlaceOrderCommand placeOrderCommand = PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .items(Arrays.asList(new OrderItem(effectiveJava.getId(), 16), new OrderItem(puzzlers.getId(), 7)))
                .build();

        PlaceOrderResponse response = placeOrder.placeOrder(placeOrderCommand);
        System.out.println("Created ORDER with id: " + response.getOrderId());


        //list of all orders
        queryOrder.findAll()
                .forEach(order -> System.out.println("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS: " + order));

    }


//    private void findByTitle() {
//        List<Book> booksByTitle = catalog.findByTitle(title);
//        booksByTitle.forEach(System.out::println);
//    }


    private void initData() {

        Author joushua = Author.builder()
                .name("Joushua")
                .lastName("Bloch")
                .build();

        Author neal = Author.builder()
                .name("Neal")
                .lastName("Gafter")
                .build();

        authorRepository.save(joushua);
        authorRepository.save(neal);

        CreateBookCommand effectiveJava = new CreateBookCommand(
                "Effective Java",
                Set.of(joushua.getId()),
                2005,
                new BigDecimal("79.00")
        );

        CreateBookCommand javaPuzzlers = new CreateBookCommand(
                "Java Puzzlers",
                Set.of(joushua.getId(), neal.getId()),
                2018,
                new BigDecimal("99.00")
        );


        catalog.addBook(effectiveJava);
        catalog.addBook(javaPuzzlers);
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
