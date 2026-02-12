package com.example.TaskApp.config;

import com.example.TaskApp.models.Role;
import com.example.TaskApp.models.User;
import com.example.TaskApp.repository.UserRepositry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initData(UserRepositry userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if any user exists
            if (userRepository.count() == 0) {
                User manager = new User();
                manager.setDisplayName("System Manager");
                manager.setEmail("admin@taskapp.com");
                manager.setPassword(passwordEncoder.encode("password"));
                manager.setRole(Role.MANAGER);
                userRepository.save(manager);
                System.out.println("Default Manager created: admin@taskapp.com / password");
            }
        };
    }
}
