package com.example.clickanddrive.dtosample.requests;

import androidx.annotation.NonNull;

// Request for when a user want to change his information
public class UpdateProfileRequest {
    private String name;
    private String surname;
    private String email;
    private String address;
    private String phone;
    // Only if the user changing his information is a driver
    private UpdateVehicleRequest vehicle;

    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String name, String surname, String email, String address, String phone, UpdateVehicleRequest vehicle) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.vehicle = vehicle;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UpdateVehicleRequest getVehicle() {
        return vehicle;
    }

    public void setVehicle(UpdateVehicleRequest vehicle) {
        this.vehicle = vehicle;
    }

    @NonNull
    @Override
    public String toString() {
        return "UpdateProfileRequest{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", vehicle=" + vehicle +
                '}';
    }
}
