package ostasp.bookapp.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ostasp.bookapp.catalog.domain.Book;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    Optional<Book> findOneByTitleContainsIgnoreCase(String title);


    @Query("SELECT b " +
            " FROM Book b JOIN b.authors a " +
            " WHERE " +
            " LOWER(b.title) LIKE LOWER(CONCAT('%',:title,'%')) " +
            " AND " +
            " (" +
            " LOWER (a.firstName) LIKE LOWER(CONCAT('%',:author,'%')) " +
            " OR LOWER (a.lastName) LIKE LOWER(CONCAT('%',:author,'%')) " +
            " ) ")
    List<Book> findByTitleAndAuthor(@Param("title") String title, @Param("author") String author);

//    @Override
//    public List<Book> findByTitleAndAuthor(String title, String author) {
//        return bookRepository
//                .findAll()
//                .stream()
////                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
//                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
//                .collect(Collectors.toList());
//    }


}
