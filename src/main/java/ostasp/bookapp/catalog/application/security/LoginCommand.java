package ostasp.bookapp.catalog.application.security;

import lombok.Data;

@Data
public class LoginCommand {

    private String username;
    private String password;
}
