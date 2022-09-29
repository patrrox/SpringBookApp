package ostasp.bookaro.order.domain;

import lombok.Getter;
import ostasp.bookaro.catalog.domain.Book;

@Getter
public class OrderItem {

    private Book book;
    private int quantity;
}
