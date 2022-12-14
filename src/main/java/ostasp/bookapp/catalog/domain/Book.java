package ostasp.bookapp.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ostasp.bookapp.jpa.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = "authors")
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Book extends BaseEntity {

    @Column(unique = true)
    private String title;
    private Integer bookYear;
    private BigDecimal price;
    private Long coverId;
    private Long available;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable
    @JsonIgnoreProperties("books")
    private Set<Author> authors = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Book(String title, Integer bookYear, BigDecimal price, Long available) {
        this.title = title;
        this.bookYear = bookYear;
        this.price = price;
        this.available = available;
    }

    public void addAuthor(Author author) {
        authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author) {
        authors.remove(author);
        author.getBooks().remove(this);
    }

    public void removeAuthors() {
        authors.forEach(author -> author.getBooks().remove(this));
        authors.clear();
    }
}
