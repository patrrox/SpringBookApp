package ostasp.bookapp.catalog.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ostasp.bookapp.catalog.application.port.AuthorUseCase;
import ostasp.bookapp.catalog.db.AuthorJpaRepository;
import ostasp.bookapp.catalog.domain.Author;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthorService implements AuthorUseCase {

    private final AuthorJpaRepository repository;

    @Override
    public List<Author> findAll() {
        return repository.findAll();
    }
}
