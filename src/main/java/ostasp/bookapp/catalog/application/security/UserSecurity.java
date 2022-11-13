package ostasp.bookapp.catalog.application.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;

@Configuration
public class UserSecurity {
    
    public boolean isOwnerOrAdmin(String objectOwner, User user){
        return isAdmin(user) || isOwner(objectOwner,user);
    }

    private boolean isOwner(String objectOwner, User user) {
        return user.getUsername().equalsIgnoreCase(objectOwner);
    }

    private boolean isAdmin(User user) {
        return user.getAuthorities()
                .stream()
                .anyMatch(a-> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
    }
}
