package ostasp.bookapp.uploads.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ostasp.bookapp.jpa.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
public class Upload extends BaseEntity {

    private byte[] file;
    private String contentType;
    private String filename;

    @CreatedDate
    private LocalDateTime createdAt;


}
