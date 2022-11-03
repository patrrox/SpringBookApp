package ostasp.bookapp.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ostasp.bookapp.catalog.domain.Book;

public interface BookJpaRepository extends JpaRepository<Book, Long> {
}
