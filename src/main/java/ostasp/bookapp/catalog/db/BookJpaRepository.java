package ostasp.bookapp.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ostasp.bookapp.catalog.domain.Book;

import java.util.List;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

    List<Book> findByAuthors_firstNameContainsIgnoreCaseOrAuthors_lastNameContainsIgnoreCase(String firstName, String lastName);

    List<Book> findByTitleStartsWithIgnoreCase(String title);

    @Query(
            "SELECT b " +
                    " FROM Book b JOIN b.authors a " +
                    " WHERE " +
                    " LOWER (a.firstName) LIKE LOWER(CONCAT('%',:name,'%')) " +
                    " OR LOWER (a.lastName) LIKE LOWER(CONCAT('%',:name,'%'))")
    List<Book> findByAuthor(@Param("name") String name);
}
