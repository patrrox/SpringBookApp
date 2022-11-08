package ostasp.bookapp.order.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ostasp.bookapp.catalog.domain.Book;
import ostasp.bookapp.jpa.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OrderItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private int quantity;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public OrderItem(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }
}
