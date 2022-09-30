package ostasp.bookapp.catalog.application.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import ostasp.bookapp.catalog.application.port.CatalogUseCase;
import ostasp.bookapp.catalog.domain.Book;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/catalog")
@RestController
@AllArgsConstructor
public class CatalogController {

    private final CatalogUseCase catalog;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Book> getAll(
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> author) {
        if (title.isPresent() && author.isPresent()) {
            return catalog.findByTitleAndAuthor(title.get(), author.get());
        } else if (title.isPresent()) {
            return catalog.findByTitle(title.get());
        } else if (author.isPresent()) {
            return catalog.findByAuthor(author.get());
        }
        return catalog.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Book> findById(@PathVariable Long id) {
        return catalog
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse((ResponseEntity.notFound().build()));
    }


}
