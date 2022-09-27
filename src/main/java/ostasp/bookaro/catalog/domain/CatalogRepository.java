package ostasp.bookaro.catalog.domain;

import org.springframework.stereotype.Repository;

import java.util.List;
public interface  CatalogRepository {
    List<Book> findAll();

}
