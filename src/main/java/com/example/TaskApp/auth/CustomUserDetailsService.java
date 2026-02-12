package com.example.TaskApp.auth;

import com.example.TaskApp.models.User;
import com.example.TaskApp.repository.UserRepositry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositry userRepositry;

    public CustomUserDetailsService(UserRepositry userRepositry) {
        this.userRepositry = userRepositry;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepositry.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new AuthUser(user);
    }
}
