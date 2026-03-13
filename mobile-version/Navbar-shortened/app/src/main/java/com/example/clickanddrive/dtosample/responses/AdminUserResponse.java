package com.example.clickanddrive.dtosample.responses;

public class AdminUserResponse {
    private Long id;
    private String username;
    private String role;

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}