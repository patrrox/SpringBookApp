package ostasp.bookapp.order.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import ostasp.bookapp.catalog.domain.Book;

@Data
@AllArgsConstructor
public class OrderItem {

    private Book book;
    private int quantity;
}
