package ostasp.bookapp.user.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ostasp.bookapp.user.domain.UserEntity;

import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByUsernameIgnoreCase(String username);
}
