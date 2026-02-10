package com.example.clickanddrive.dtosample.requests;

import androidx.annotation.NonNull;

import com.example.clickanddrive.dtosample.enumerations.Gender;

public class DriverRegistrationRequest {
    private String email;
    private String name;
    private String surname;
    private Gender gender;
    private String address;
    private String phone;
    private VehicleRegistrationRequest vehicle;

    public DriverRegistrationRequest() {}

    public DriverRegistrationRequest(String email, String name, String surname, Gender gender, String address, String phone, VehicleRegistrationRequest vehicle) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.vehicle = vehicle;
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
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

    public VehicleRegistrationRequest getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleRegistrationRequest vehicle) {
        this.vehicle = vehicle;
    }

    @NonNull
    @Override
    public String toString() {
        return "DriverRegistrationRequest{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", gender=" + gender +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", vehicle=" + vehicle +
                '}';
    }
}
