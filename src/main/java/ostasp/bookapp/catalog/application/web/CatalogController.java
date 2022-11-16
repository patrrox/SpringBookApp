package ostasp.bookapp.catalog.application.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ostasp.bookapp.catalog.application.port.CatalogUseCase;
import ostasp.bookapp.catalog.application.port.CatalogUseCase.CreateBookCommand;
import ostasp.bookapp.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import ostasp.bookapp.catalog.application.port.CatalogUseCase.UpdateBookCoverCommand;
import ostasp.bookapp.catalog.application.port.CatalogUseCase.UpdateBookResponse;
import ostasp.bookapp.catalog.domain.Author;
import ostasp.bookapp.catalog.domain.Book;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/catalog")
@RestController
@AllArgsConstructor
public class CatalogController {

    private final CatalogUseCase catalog;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RestBook> getAll(
            HttpServletRequest request,
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> author) {

        List<Book> books;
        if (title.isPresent() && author.isPresent()) {
            books = catalog.findByTitleAndAuthor(title.get(), author.get());
        } else if (title.isPresent()) {
            books = catalog.findByTitle(title.get());
        } else if (author.isPresent()) {
            books = catalog.findByAuthor(author.get());
        } else {
            books = catalog.findAll();
        }
        return books.stream()
                .map(book -> toRestBook(book, request))
                .collect(Collectors.toList());
    }

    private RestBook toRestBook(Book book, HttpServletRequest request) {
        String coverUrl = Optional.ofNullable(book.getCoverId())
                .map(coverId -> {
                    return ServletUriComponentsBuilder
                            .fromContextPath(request)
                            .path("/uploads/{id}/file")
                            .build(book.getCoverId())
                            .toASCIIString();
                }).orElse(null);

        return new RestBook(
                book.getId(),
                book.getTitle(),
                book.getBookYear(),
                book.getPrice(),
                coverUrl,
                book.getAvailable(),
                toRestAuthors(book.getAuthors()));
    }

    private Set<RestAuthor> toRestAuthors(Set<Author> authors) {
        return authors.stream()
                .map(x -> new RestAuthor(x.getName()))
                .collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> findById(@PathVariable Long id) {
        return catalog
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse((ResponseEntity.notFound().build()));
    }


    @Secured({"ROLE_ADMIN"})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateBook(@PathVariable Long id, @RequestBody RestBookCommand command) {
        UpdateBookResponse response = catalog.updateBook(command.toUpdateBookCommand(id));

        if (!response.isSuccess()) {
            String message = String.join(", ", response.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @Secured({"ROLE_ADMIN"})
    @PutMapping(value = "/{id}/cover", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addBookCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        log.info("Got file: " + file.getOriginalFilename());
        catalog.updateBookCover(new UpdateBookCoverCommand(
                id,
                file.getBytes(),
                file.getContentType(),
                file.getOriginalFilename()
        ));

    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookCover(@PathVariable Long id) {
        catalog.removeBookCover(id);
    }


    @Secured({"ROLE_ADMIN"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addBook(@Valid @RequestBody RestBookCommand command) {
        Book savedBook = catalog.addBook(command.toCreateCommand());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/" + savedBook.getId().toString()).build().toUri();
        return ResponseEntity.created(uri).build();
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        catalog.removeById(id);
    }


    @Data
    private static class RestBookCommand {
        @NotBlank
        private String title;
        @NotEmpty
        private Set<Long> authors;

        @NotNull
        private Integer year;

        @NotNull
        @PositiveOrZero
        private Long available;

        @NotNull
        @DecimalMin("0.00")
        private BigDecimal price;

        CreateBookCommand toCreateCommand() {
            return new CreateBookCommand(title, authors, year, price, available);
        }

        UpdateBookCommand toUpdateBookCommand(Long id) {
            return new UpdateBookCommand(id, title, authors, year, price);
        }
    }


}
