package ostasp.bookapp.catalog.application.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ostasp.bookapp.catalog.application.port.CatalogUseCase;
import ostasp.bookapp.catalog.application.port.CatalogUseCase.CreateBookCommand;
import ostasp.bookapp.catalog.db.AuthorJpaRepository;
import ostasp.bookapp.catalog.domain.Author;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase.OrderItemCommand;
import ostasp.bookapp.order.application.port.QueryOrderUseCase;
import ostasp.bookapp.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final CatalogUseCase catalog;
    private final ManipulateOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final AuthorJpaRepository authorRepository;


    @PostMapping("/data")
    @Transactional
    public void initialize() {
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
        ManipulateOrderUseCase.PlaceOrderCommand placeOrderCommand = ManipulateOrderUseCase.PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .items(Arrays.asList(new OrderItemCommand(effectiveJava.getId(), 16), new OrderItemCommand(puzzlers.getId(), 7)))
                .build();

        ManipulateOrderUseCase.PlaceOrderResponse response = placeOrder.placeOrder(placeOrderCommand);
        log.info("Created ORDER with id: " + response.getOrderId());


        //list of all orders
        queryOrder.findAll()
                .forEach(order -> log.info("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS: " + order));

    }

    private void initData() {

        Author joshua = new Author("Joshua", "Bloch");
        Author neal = new Author("Neal", "Gafter");
        authorRepository.save(joshua);
        authorRepository.save(neal);

        CreateBookCommand effectiveJava = new CreateBookCommand(
                "Effective Java",
                Set.of(joshua.getId()),
                2005,
                new BigDecimal("79.00"),
                50L
        );

        CreateBookCommand javaPuzzlers = new CreateBookCommand(
                "Java Puzzlers",
                Set.of(joshua.getId(), neal.getId()),
                2018,
                new BigDecimal("99.00"),
                50L
        );


        catalog.addBook(effectiveJava);
        catalog.addBook(javaPuzzlers);
    }

}
