package ostasp.bookapp.order.domain;


import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Recipient {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String email;

}
