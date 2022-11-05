package ostasp.bookapp.catalog.application.port;

import ostasp.bookapp.catalog.domain.Author;
import ostasp.bookapp.catalog.domain.Book;

import java.util.List;

public interface AuthorUseCase {

    List<Author> findAll();
}
