package ostasp.bookapp.order.domain;


import lombok.*;
import ostasp.bookapp.jpa.BaseEntity;

import javax.persistence.Entity;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Recipient extends BaseEntity {

    private String email;
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;

}
