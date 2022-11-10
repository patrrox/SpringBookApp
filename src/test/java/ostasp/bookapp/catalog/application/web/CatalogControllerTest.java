package ostasp.bookapp.catalog.application.web;

import com.fasterxml.classmate.util.ResolvedTypeCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ostasp.bookapp.catalog.application.port.CatalogUseCase;
import ostasp.bookapp.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CatalogController.class})
class CatalogControllerTest {

    @MockBean
    CatalogUseCase catalogUseCase;

    @Autowired
    CatalogController controller;

    @Test
    public void shouldGetAllBooks() {
        //given
        Book effective  = new Book("Effective Java", 2005, new BigDecimal("199.90"),10L);
        Book concurrency = new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"),10L);
        Mockito.when(catalogUseCase.findAll()).thenReturn(List.of(effective, concurrency));
        //when
        List<Book> all = controller.getAll(Optional.empty(), Optional.empty());


        //then
        assertEquals(2, all.size());
    }
}