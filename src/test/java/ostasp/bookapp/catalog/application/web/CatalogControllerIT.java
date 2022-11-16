package ostasp.bookapp.catalog.application.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import ostasp.bookapp.catalog.application.port.CatalogUseCase;
import ostasp.bookapp.catalog.db.AuthorJpaRepository;
import ostasp.bookapp.catalog.domain.Author;
import ostasp.bookapp.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CatalogControllerIT {

    @Autowired
    CatalogController controller;
    @Autowired
    CatalogUseCase catalogUseCase;
    @Autowired
    AuthorJpaRepository authorJpaRepository;

    @Test
    public void getAllBooks() {
        //given
        givenEffectiveJava();
        givenJavaConcurrencyInPractice();
        //when
        List<RestBook> all = controller.getAll(getMockHttpServletRequest(), Optional.empty(), Optional.empty());
        //then
        assertEquals(2, all.size());
    }

    private static MockHttpServletRequest getMockHttpServletRequest() {
        return new MockHttpServletRequest();
    }

    @Test
    public void getAllBooksByAuthor() {
        //given
        givenEffectiveJava();
        givenJavaConcurrencyInPractice();
        //when
        List<RestBook> all = controller.getAll(getMockHttpServletRequest(),Optional.empty(), Optional.of("Bloch"));

        //then
        assertEquals(1, all.size());
        assertEquals("Effective Java", all.get(0).getTitle());
    }


    @Test
    public void getAllBooksByTitle() {
        //given
        givenEffectiveJava();
        givenJavaConcurrencyInPractice();
        //when
        List<RestBook> all = controller.getAll(getMockHttpServletRequest(),Optional.of("Java Concurrency in Practice"), Optional.empty());

        //then
        assertEquals(1, all.size());
        assertEquals("Java Concurrency in Practice", all.get(0).getTitle());
    }


    private void givenJavaConcurrencyInPractice() {
        Author goetz = authorJpaRepository.save(new Author("Brian Goetz"));
        catalogUseCase.addBook(new CatalogUseCase.CreateBookCommand(
                "Java Concurrency in Practice",
                Set.of(goetz.getId()),
                2006,
                new BigDecimal("129.90"),
                50L
        ));
    }

    private void givenEffectiveJava() {
        Author bloch = authorJpaRepository.save(new Author("Joshua Bloch"));
        catalogUseCase.addBook(new CatalogUseCase.CreateBookCommand(
                "Effective Java",
                Set.of(bloch.getId()),
                2005,
                new BigDecimal("99.90"),
                50L
        ));
    }
}