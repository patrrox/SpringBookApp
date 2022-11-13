package ostasp.bookapp;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class BookAppSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .mvcMatchers(HttpMethod.GET,"/catalog/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
        return http.build();

    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        // configure Web security...
//        return null;
//    }
}
