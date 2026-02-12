package com.example.TaskApp.dto;

import com.example.TaskApp.models.Role;

public class UserRequestDTO {

    private String displayName;
    private String email;
    private Role role;
    private String password;

    public UserRequestDTO() {
    }

    public UserRequestDTO(String displayName, String email, Role role, String password) {
        this.displayName = displayName;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
