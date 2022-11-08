package ostasp.bookapp.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ostasp.bookapp.catalog.domain.Author;

import java.util.List;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {

    @Query("SELECT DISTINCT a FROM Author a JOIN FETCH a.books")
    List<Author> findAllEager();
}
