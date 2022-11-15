package ostasp.bookapp.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
public class UserSecurity {
    
    public boolean isOwnerOrAdmin(String objectOwner, UserDetails user){
        return isAdmin(user) || isOwner(objectOwner,user);
    }

    private boolean isOwner(String objectOwner, UserDetails user) {
        return user.getUsername().equalsIgnoreCase(objectOwner);
    }

    private boolean isAdmin(UserDetails user) {
        return user.getAuthorities()
                .stream()
                .anyMatch(a-> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
    }
}
