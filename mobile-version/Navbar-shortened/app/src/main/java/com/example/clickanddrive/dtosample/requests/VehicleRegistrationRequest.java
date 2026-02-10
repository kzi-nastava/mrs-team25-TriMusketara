package com.example.clickanddrive.dtosample.requests;

import androidx.annotation.NonNull;

import com.example.clickanddrive.dtosample.enumerations.VehicleType;

public class VehicleRegistrationRequest {
    private String model;
    private VehicleType type;
    private String registration;
    private int seats;
    private boolean isBabyFriendly;
    private boolean isPetFriendly;

    public VehicleRegistrationRequest() {}

    public VehicleRegistrationRequest(String model, VehicleType type, String registration, int seats, boolean isBabyFriendly, boolean isPetFriendly) {
        this.model = model;
        this.type = type;
        this.registration = registration;
        this.seats = seats;
        this.isBabyFriendly = isBabyFriendly;
        this.isPetFriendly = isPetFriendly;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public boolean isBabyFriendly() {
        return isBabyFriendly;
    }

    public void setBabyFriendly(boolean babyFriendly) {
        isBabyFriendly = babyFriendly;
    }

    public boolean isPetFriendly() {
        return isPetFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        isPetFriendly = petFriendly;
    }

    @NonNull
    @Override
    public String toString() {
        return "VehicleRegistrationRequest{" +
                "model='" + model + '\'' +
                ", type=" + type +
                ", registration='" + registration + '\'' +
                ", seats=" + seats +
                ", isBabyFriendly=" + isBabyFriendly +
                ", isPetFriendly=" + isPetFriendly +
                '}';
    }
}
