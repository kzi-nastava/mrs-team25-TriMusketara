package com.example.clickanddrive.dtosample.responses;

import androidx.annotation.NonNull;

import com.example.clickanddrive.dtosample.enumerations.DriverStatus;

public class DriverRegistrationResponse {
    private Long id;
    private String email;
    private String name;
    private String surname;
    private DriverStatus status;

    public DriverRegistrationResponse() {}

    public DriverRegistrationResponse(Long id, String email, String name, String surname, DriverStatus status) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return "DriverRegistrationResponse{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", status=" + status +
                '}';
    }
}
