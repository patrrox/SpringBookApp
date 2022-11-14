package ostasp.bookapp.catalog.application.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class BookAppSecurityConfiguration {


    @Bean
    User systemUser() {
        return new User("systemUser", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }


    //http
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        // Get AuthenticationManager
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.csrf().disable();
        http.authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/catalog/**", "/uploads/**", "/authors/**").permitAll()
                .mvcMatchers(HttpMethod.POST, "/orders","/login").permitAll()
                .anyRequest().authenticated()
                .and()
                    .httpBasic()
                .and()
                    .addFilterBefore(authenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
        .authenticationManager(authenticationManager);
        return http.build();

    }

    private JsonUsernameAuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager) {
        JsonUsernameAuthenticationFilter filter = new JsonUsernameAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    //inMemory
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.builder()
                .username("marek@example.org")
                .password("{noop}xxx")
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin@example.org")
                .password("{noop}xxx")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//
//        return (web -> {});
//    }
}
