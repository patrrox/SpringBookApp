package ostasp.bookapp.catalog.application.web;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ostasp.bookapp.catalog.application.port.CatalogUseCase;
import ostasp.bookapp.catalog.db.AuthorJpaRepository;
import ostasp.bookapp.catalog.domain.Author;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.order.application.port.ManipulateOrderUseCase;
import ostasp.bookapp.order.application.port.QueryOrderUseCase;
import ostasp.bookapp.order.domain.OrderItem;
import ostasp.bookapp.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;

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
                .items(Arrays.asList(new OrderItem(effectiveJava.getId(), 16), new OrderItem(puzzlers.getId(), 7)))
                .build();

        ManipulateOrderUseCase.PlaceOrderResponse response = placeOrder.placeOrder(placeOrderCommand);
        System.out.println("Created ORDER with id: " + response.getOrderId());


        //list of all orders
        queryOrder.findAll()
                .forEach(order -> System.out.println("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS: " + order));

    }

    private void initData() {

        Author joshua = new Author("Joshua", "Bloch");
        Author neal = new Author("Neal", "Gafter");
        authorRepository.save(joshua);
        authorRepository.save(neal);

        CatalogUseCase.CreateBookCommand effectiveJava = new CatalogUseCase.CreateBookCommand(
                "Effective Java",
                Set.of(joshua.getId()),
                2005,
                new BigDecimal("79.00")
        );

        CatalogUseCase.CreateBookCommand javaPuzzlers = new CatalogUseCase.CreateBookCommand(
                "Java Puzzlers",
                Set.of(joshua.getId(), neal.getId()),
                2018,
                new BigDecimal("99.00")
        );


        catalog.addBook(effectiveJava);
        catalog.addBook(javaPuzzlers);
    }

}
