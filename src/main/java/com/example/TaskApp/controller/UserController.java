package com.example.TaskApp.controller;

import com.example.TaskApp.dto.UserRequestDTO;
import com.example.TaskApp.dto.UserResponseDTO;
import com.example.TaskApp.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /* CREATE USER */
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MANAGER')")
    public UserResponseDTO createUser(@RequestBody UserRequestDTO userRequestDTO) {
        return userService.createUser(userRequestDTO);
    }

    /* GET USER BY ID */
    @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable int id) {
        return userService.getUserById(id);
    }

    /* GET USERS BY ROLE */
    @GetMapping("/role/{role}")
    public List<UserResponseDTO> getUsersByRole(@PathVariable String role) {
        return userService.getUsersByRole(role);
    }
}
