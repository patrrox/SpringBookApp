package ostasp.bookapp.user.application;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ostasp.bookapp.user.application.port.UserRegistationUseCase;
import ostasp.bookapp.user.db.UserEntityRepository;
import ostasp.bookapp.user.domain.UserEntity;

@Service
@AllArgsConstructor
public class UserService implements UserRegistationUseCase {

    private final UserEntityRepository repository;
    private final PasswordEncoder encoder;

    @Transactional
    @Override
    public RegisterResponse register(String username, String password) {
        if(repository.findByUsernameIgnoreCase(username).isPresent()){
            //failure
            return RegisterResponse.failure("Account already exists");
        }
        UserEntity user = new UserEntity(username,encoder.encode(password));
        UserEntity save = repository.save(user);
        return RegisterResponse.success(save);
    }
}
