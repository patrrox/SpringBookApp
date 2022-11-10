package ostasp.bookapp.order.price;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ostasp.bookapp.catalog.application.web.CatalogController;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.order.domain.Order;
import ostasp.bookapp.order.domain.OrderItem;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {PriceService.class})
@ExtendWith(SpringExtension.class)
class PriceServiceTest {


    @Autowired
    PriceService priceService;

    @Test
    public void calculatesTotalPriceOfEmptyOrder() {
        //given
        Order order = Order
                .builder()
                .build();
        //when
        OrderPrice price = priceService.calculatePrice(order);
        //then
        assertEquals(BigDecimal.ZERO, price.finalPrice());

    }

    @Test
    public void calculatesTotalPrice() {
        //given
        Book book1 = new Book();
        book1.setPrice(new BigDecimal("12.50"));
        Book book2 = new Book();
        book2.setPrice(new BigDecimal("33.99"));

        Set<OrderItem> items = new HashSet<>(
                Arrays.asList(
                        new OrderItem(book1, 2),
                        new OrderItem(book2, 5)
                )
        );

        Order order = Order.builder()
                .items(items)
                .build();


        //when
        OrderPrice price = priceService.calculatePrice(order);
        //then
        assertEquals(BigDecimal.valueOf(194.95), price.finalPrice());
        assertEquals(BigDecimal.valueOf(194.95), price.getItemsPrice());


    }


}