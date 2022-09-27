package ostasp.bookaro;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class BookaroOnlineStoreApplication implements CommandLineRunner {


	public static void main(String[] args) {
		SpringApplication.run(BookaroOnlineStoreApplication.class, args);
	}


	private final CatalogService catalogService;

	public BookaroOnlineStoreApplication(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	@Override
	public void run(String... args) throws Exception {

		System.out.println("Start");
//	CatalogService catalogService = new CatalogService();
		List<Book> pan_tadeusz = catalogService.findByTitle("Pan Tadeusz");
	System.out.println(pan_tadeusz);


	}
}
