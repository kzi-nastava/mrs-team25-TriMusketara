package com.example.clickanddrive.dtosample.requests;

import androidx.annotation.NonNull;

import com.example.clickanddrive.dtosample.LocationDTO;
import com.example.clickanddrive.dtosample.enumerations.VehicleType;

import java.time.LocalDateTime;
import java.util.List;

public class CreateRideRequest {
    private Long passengerId; // Which passenger created the ride

    private LocationDTO origin;
    private LocationDTO destination;
    private List<LocationDTO> stops;
    private List<String> passengerEmails;
    private VehicleType vehicleType;
    private LocalDateTime scheduledTime;
    private boolean babyFriendly;
    private boolean petFriendly;
    private int durationMinutes;
    private double distanceKm;

    public CreateRideRequest() {}

    public CreateRideRequest(Long passengerId, LocationDTO origin, LocationDTO destination, List<LocationDTO> stops, List<String> passengerEmails, VehicleType vehicleType, LocalDateTime scheduledTime, boolean babyFriendly, boolean petFriendly, int durationMinutes, double distanceKm) {
        this.passengerId = passengerId;
        this.origin = origin;
        this.destination = destination;
        this.stops = stops;
        this.passengerEmails = passengerEmails;
        this.vehicleType = vehicleType;
        this.scheduledTime = scheduledTime;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
        this.durationMinutes = durationMinutes;
        this.distanceKm = distanceKm;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public LocationDTO getOrigin() {
        return origin;
    }

    public void setOrigin(LocationDTO origin) {
        this.origin = origin;
    }

    public LocationDTO getDestination() {
        return destination;
    }

    public void setDestination(LocationDTO destination) {
        this.destination = destination;
    }

    public List<LocationDTO> getStops() {
        return stops;
    }

    public void setStops(List<LocationDTO> stops) {
        this.stops = stops;
    }

    public List<String> getPassengerEmails() {
        return passengerEmails;
    }

    public void setPassengerEmails(List<String> passengerEmails) {
        this.passengerEmails = passengerEmails;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public boolean isBabyFriendly() {
        return babyFriendly;
    }

    public void setBabyFriendly(boolean babyFriendly) {
        this.babyFriendly = babyFriendly;
    }

    public boolean isPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    @NonNull
    @Override
    public String toString() {
        return "CreateRideRequest{" +
                "passengerId=" + passengerId +
                ", origin=" + origin +
                ", destination=" + destination +
                ", stops=" + stops +
                ", passengerEmails=" + passengerEmails +
                ", vehicleType=" + vehicleType +
                ", scheduledTime=" + scheduledTime +
                ", babyFriendly=" + babyFriendly +
                ", petFriendly=" + petFriendly +
                ", durationMinutes=" + durationMinutes +
                ", distanceKm=" + distanceKm +
                '}';
    }
}
