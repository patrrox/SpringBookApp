package ostasp.bookapp.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ostasp.bookapp.catalog.domain.Author;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {
}
