package ostasp.bookapp.catalog.application.web;

import lombok.Value;
import ostasp.bookapp.catalog.domain.Author;

import java.math.BigDecimal;
import java.util.Set;

@Value
public class RestBook {

    Long id;
    String title;
    Integer year;
    BigDecimal price;
    String coverUrl;
    Long available;
    Set<RestAuthor> authors;
}
