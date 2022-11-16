package ostasp.bookapp.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ostasp.bookapp.catalog.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

    List<Book> findByAuthors_nameContainsIgnoreCase(String name);

    List<Book> findByTitleStartsWithIgnoreCase(String title);

    @Query(
            "SELECT b " +
                    " FROM Book b JOIN b.authors a " +
                    " WHERE " +
                    " LOWER (a.name) LIKE LOWER(CONCAT('%',:name,'%')) ")
    List<Book> findByAuthor(@Param("name") String name);

    Optional<Book> findOneByTitleContainsIgnoreCase(String title);

    @Query("SELECT b " +
            " FROM Book b JOIN b.authors a " +
            " WHERE " +
            " LOWER(b.title) LIKE LOWER(CONCAT('%',:title,'%')) " +
            " AND " +
            " LOWER (a.name) LIKE LOWER(CONCAT('%',:author,'%')) " )
    List<Book> findByTitleAndAuthor(@Param("title") String title, @Param("author") String author);

}
