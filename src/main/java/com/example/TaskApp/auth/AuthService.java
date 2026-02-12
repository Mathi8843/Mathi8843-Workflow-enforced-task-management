package com.example.TaskApp.auth;

import com.example.TaskApp.dto.AuthResponse;
import com.example.TaskApp.dto.LoginRequest;
import com.example.TaskApp.dto.RegisterRequest;
import com.example.TaskApp.models.Role;
import com.example.TaskApp.models.User;
import com.example.TaskApp.repository.UserRepositry;
import com.example.TaskApp.exception.InvalidRoleException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepositry userRepositry;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepositry userRepositry,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            AuthenticationManager authenticationManager) {
        this.userRepositry = userRepositry;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepositry.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setDisplayName(request.getDisplayName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            if (request.getRole() != null && !request.getRole().isEmpty()) {
                user.setRole(Role.valueOf(request.getRole().toUpperCase()));
            } else {
                user.setRole(Role.DEVELOPER); // Default role
            }
        } catch (IllegalArgumentException e) {
            // Assuming InvalidRoleException exists or I should throw
            // IllegalArgumentException
            throw new IllegalArgumentException("Invalid role provided");
        }

        userRepositry.save(user);

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepositry.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token);
    }
}
