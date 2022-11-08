package ostasp.bookapp.order.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ostasp.bookapp.order.domain.Recipient;

import java.util.Optional;

public interface RecipientJpaRepository extends JpaRepository<Recipient,Long> {

    Optional<Recipient> findByEmailIgnoreCase(String email);

}
