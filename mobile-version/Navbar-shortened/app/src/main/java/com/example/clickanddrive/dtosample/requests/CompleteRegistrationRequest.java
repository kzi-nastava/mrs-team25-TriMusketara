package com.example.clickanddrive.dtosample.requests;

import androidx.annotation.NonNull;

// Driver registration completion
public class CompleteRegistrationRequest {
    private String token;
    private String password;
    private String confirmPassword;

    public CompleteRegistrationRequest() {}

    public CompleteRegistrationRequest(String token, String password, String confirmPassword) {
        this.token = token;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        return "CompleteRegistrationRequest{" +
                "token='" + token + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }
}
