package ostasp.bookapp.order.domain;


import lombok.*;

import javax.persistence.Embeddable;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Recipient {

    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String email;

}
