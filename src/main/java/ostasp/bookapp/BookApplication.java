package ostasp.bookapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import ostasp.bookapp.order.application.OrdersProperties;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties({OrdersProperties.class})
public class BookApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplateBuilder().build();
    }
}
