package com.example.TaskApp.dto;

import com.example.TaskApp.models.Role;

public class UserResponseDTO {

    private int userId;
    private String userName;
    private Role role;

    public UserResponseDTO() {
    }

    public UserResponseDTO(int userId, String userName, Role role) {
        this.userId = userId;
        this.userName = userName;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
