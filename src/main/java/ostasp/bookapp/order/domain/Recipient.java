package ostasp.bookapp.order.domain;


import lombok.*;
import ostasp.bookapp.jpa.BaseEntity;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Recipient extends BaseEntity {

    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String email;

}
