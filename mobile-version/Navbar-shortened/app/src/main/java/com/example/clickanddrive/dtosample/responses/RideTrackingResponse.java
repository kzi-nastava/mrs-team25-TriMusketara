package com.example.clickanddrive.dtosample.responses;

import com.example.clickanddrive.dtosample.LocationDTO;

public class RideTrackingResponse {
    private Long rideId;
    private LocationDTO vehicleLocation;
    private int estimatedTimeInMinutes;
    private String status;
    private double progressPercent;

    public Long getRideId() {
        return rideId;
    }

    public LocationDTO getVehicleLocation() {
        return vehicleLocation;
    }

    public int getEstimatedTimeInMinutes() {
        return estimatedTimeInMinutes;
    }

    public String getStatus() {
        return status;
    }

    public double getProgressPercent() {
        return progressPercent;
    }
}