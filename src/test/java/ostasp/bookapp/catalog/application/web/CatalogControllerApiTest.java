package ostasp.bookapp.catalog.application.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import ostasp.bookapp.catalog.application.port.CatalogUseCase;
import ostasp.bookapp.catalog.domain.Book;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(webEnvironment = RANDOM_PORT)
class CatalogControllerApiTest {

    @LocalServerPort
    private int port;
    @MockBean
    CatalogUseCase catalogUseCase;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void getAllBooks(){
        //given
        Book effective  = new Book("Effective Java", 2005, new BigDecimal("199.90"),10L);
        Book concurrency = new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"),10L);
        Mockito.when(catalogUseCase.findAll()).thenReturn(List.of(effective, concurrency));

        ParameterizedTypeReference<List<Book>> type= new ParameterizedTypeReference<>(){};
        RequestEntity<Void> request = RequestEntity.get(URI.create("http://localhost:" + port + "/catalog")).build();
        ResponseEntity<List<Book>> response = restTemplate.exchange(request, type);

        //then
        assertEquals(2, response.getBody().size());
    }
}