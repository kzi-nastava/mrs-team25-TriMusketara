package com.example.clickanddrive.dtosample.responses;

import com.example.clickanddrive.dtosample.LocationDTO;

import java.util.List;

public class AdminRideDetailsResponse {
    private Long rideId;
    private String startAddress;
    private String endAddress;

    private LocationDTO origin;
    private LocationDTO destination;

    private String startTime;
    private String endTime;
    private boolean canceled;
    private String canceledBy;
    private double price;
    private boolean panicTriggered;
    private String status;

    private Long driverId;
    private String driverName;

    private List<UserProfileResponseDTO> passengers;

    private Integer rating;
    private String comment;

    public Long getRideId() {
        return rideId;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public LocationDTO getOrigin() {
        return origin;
    }

    public LocationDTO getDestination() {
        return destination;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public String getCanceledBy() {
        return canceledBy;
    }

    public double getPrice() {
        return price;
    }

    public boolean isPanicTriggered() {
        return panicTriggered;
    }

    public String getStatus() {
        return status;
    }

    public Long getDriverId() {
        return driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public List<UserProfileResponseDTO> getPassengers() {
        return passengers;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}