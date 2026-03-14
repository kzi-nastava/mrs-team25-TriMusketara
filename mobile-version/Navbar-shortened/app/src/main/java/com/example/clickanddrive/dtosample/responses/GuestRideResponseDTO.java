package com.example.clickanddrive.dtosample.responses;

import java.io.Serializable;

public class GuestRideResponseDTO implements Serializable {
    private Long id;
    private String status;
    private int estimatedTimeMinutes;
    private double distanceKm;

    public GuestRideResponseDTO() {
    }

    public GuestRideResponseDTO(Long id, String status, int estimatedTimeMinutes, double distanceKm) {
        this.id = id;
        this.status = status;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.distanceKm = distanceKm;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public int getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public double getDistanceKm() {
        return distanceKm;
    }
}