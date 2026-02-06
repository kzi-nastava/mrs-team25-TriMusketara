package com.example.clickanddrive.dtosample.requests;

import androidx.annotation.NonNull;

import com.example.clickanddrive.dtosample.enumerations.VehicleType;

// Request for when a driver wants to change his vehicle information
public class UpdateVehicleRequest {
    private String model;
    private VehicleType type;
    private String registration;
    private boolean isBabyFriendly;
    private boolean isPetFriendly;

    public UpdateVehicleRequest() {}

    public UpdateVehicleRequest(String model, VehicleType type, String registration, boolean isBabyFriendly, boolean isPetFriendly) {
        this.model = model;
        this.type = type;
        this.registration = registration;
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
        return "UpdateVehicleRequest{" +
                "model='" + model + '\'' +
                ", type=" + type +
                ", registration='" + registration + '\'' +
                ", isBabyFriendly=" + isBabyFriendly +
                ", isPetFriendly=" + isPetFriendly +
                '}';
    }
}
