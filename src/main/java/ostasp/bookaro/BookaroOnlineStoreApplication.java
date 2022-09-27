package ostasp.bookaro;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ostasp.bookaro.catalog.application.CatalogController;
import ostasp.bookaro.catalog.domain.Book;
import ostasp.bookaro.catalog.domain.CatalogService;

import java.util.List;

@SpringBootApplication
public class BookaroOnlineStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookaroOnlineStoreApplication.class, args);
	}

}
