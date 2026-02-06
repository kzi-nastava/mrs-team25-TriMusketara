package com.example.clickanddrive.dtosample.responses;

import androidx.annotation.NonNull;

import com.example.clickanddrive.dtosample.enumerations.VehicleType;

// This response is for when we are getting vehicle info from backend
public class VehicleResponse {
    private Long id;
    private String model;
    private VehicleType type;
    private String registration;
    private int seats;
    private Boolean isBabyFriendly;
    private Boolean isPetFriendly;

    public VehicleResponse() {}

    public VehicleResponse(Long id, String model, VehicleType type, String registration, Boolean isBabyFriendly, Boolean isPetFriendly) {
        this.id = id;
        this.model = model;
        this.type = type;
        this.registration = registration;
        this.isBabyFriendly = isBabyFriendly;
        this.isPetFriendly = isPetFriendly;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getBabyFriendly() {
        return isBabyFriendly;
    }

    public void setBabyFriendly(Boolean babyFriendly) {
        isBabyFriendly = babyFriendly;
    }

    public Boolean getPetFriendly() {
        return isPetFriendly;
    }

    public void setPetFriendly(Boolean petFriendly) {
        isPetFriendly = petFriendly;
    }

    @NonNull
    @Override
    public String toString() {
        return "VehicleResponse{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", type=" + type +
                ", registration='" + registration + '\'' +
                ", seats=" + seats +
                ", isBabyFriendly=" + isBabyFriendly +
                ", isPetFriendly=" + isPetFriendly +
                '}';
    }
}
