package com.example.TaskApp.repository;

import com.example.TaskApp.models.Role;
import com.example.TaskApp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepositry extends JpaRepository<User, Integer> {

    Optional<User> findByDisplayName(String userName);

    List<User> findByRole(Role role);

    Optional<User> findByEmail(String email);
}
