package com.example.TaskApp.service;

import com.example.TaskApp.dto.UserRequestDTO;
import com.example.TaskApp.dto.UserResponseDTO;
import com.example.TaskApp.exception.UserAlreadyExistsException;
import com.example.TaskApp.exception.UserNotFoundException;
import com.example.TaskApp.models.Role;
import com.example.TaskApp.models.User;

import com.example.TaskApp.repository.UserRepositry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepositry userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public UserService(UserRepositry userRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ================= USER CREATION ================= */

    public UserResponseDTO createUser(UserRequestDTO request) {

        userRepository.findByDisplayName(request.getDisplayName())
                .ifPresent(u -> {
                    throw new UserAlreadyExistsException("User name already exists");
                });

        User user = new User();
        user.setDisplayName(request.getDisplayName());
        user.setRole(request.getRole());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    /* ================= DOMAIN ACCESS ================= */

    public User getUserEntityById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    /* ================= API ACCESS ================= */

    public UserResponseDTO getUserById(int id) {
        return mapToResponse(getUserEntityById(id));
    }

    public List<UserResponseDTO> getUsersByRole(String role) {

        Role parsedRole;
        try {
            parsedRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role");
        }

        return userRepository.findByRole(parsedRole)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /* ================= MAPPER ================= */

    private UserResponseDTO mapToResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getDisplayName());
        dto.setRole(user.getRole());
        return dto;
    }
}
