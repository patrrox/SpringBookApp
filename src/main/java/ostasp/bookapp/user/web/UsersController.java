package ostasp.bookapp.user.web;


import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ostasp.bookapp.user.application.port.UserRegistationUseCase;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UsersController {

    private final UserRegistationUseCase registerService;

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody RegisterCommand command) {

        return registerService
                .register(command.getUsername(),command.getPassword())
                .handle(
                        entity -> ResponseEntity.accepted().build(),
                        error -> ResponseEntity.badRequest().body(error)
                );
    }

    @Value
    static class RegisterCommand {
        @Email
        String username;
        @Size(min = 3, max = 100)
        String password;
    }

}
