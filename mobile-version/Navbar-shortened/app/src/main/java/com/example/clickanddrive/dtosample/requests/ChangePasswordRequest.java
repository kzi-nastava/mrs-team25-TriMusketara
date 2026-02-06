package com.example.clickanddrive.dtosample.requests;

import androidx.annotation.NonNull;

// Request when a user wants to change his password
public class ChangePasswordRequest {
    private Long id;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    public ChangePasswordRequest() {}

    public ChangePasswordRequest(Long id, String currentPassword, String newPassword, String confirmPassword) {
        this.id = id;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @NonNull
    @Override
    public String toString() {
        return "ChangePasswordRequest{" +
                "id=" + id +
                ", currentPassword='" + currentPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }
}
