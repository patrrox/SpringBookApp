package ostasp.bookapp.catalog.application.web;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ostasp.bookapp.catalog.application.port.AuthorUseCase;
import ostasp.bookapp.catalog.domain.Author;

import java.util.List;

@RestController
@RequestMapping("/authors")
@AllArgsConstructor
public class AuthorsController {

    private final AuthorUseCase authors;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<Author> findAll(){
        return authors.findAll();
    }


}
