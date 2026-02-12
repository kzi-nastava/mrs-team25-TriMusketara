package com.example.clickanddrive.dtosample.responses;

import java.io.Serializable;

public class GuestRideResponseDTO implements Serializable {
    private Long id;
    private String status;
    private int estimatedTime;
    private double distance;

    public GuestRideResponseDTO(Long id, String status, int estimatedTime, double distance) {
        this.id = id;
        this.status = status;
        this.estimatedTime = estimatedTime;
        this.distance = distance;
    }

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public int getEstimatedTime() { return estimatedTime; }
    public double getDistance() { return distance; }
}
