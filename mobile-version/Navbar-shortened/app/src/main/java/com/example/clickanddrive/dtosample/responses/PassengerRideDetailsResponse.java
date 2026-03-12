package com.example.clickanddrive.dtosample.responses;

import com.example.clickanddrive.dtosample.LocationDTO;

import java.util.List;

public class PassengerRideDetailsResponse {
    private Long id;
    private String driverEmail;
    private String driverName;
    private Double totalPrice;
    private String startTime;
    private String endTime;
    private String status;
    private LocationDTO origin;
    private LocationDTO destination;
    private int driverRating;
    private int vehicleRating;
    private List<String> inconsistencyReports;
    private boolean petFriendly;
    private boolean babyFriendly;

    public Long getId() {
        return id;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public String getDriverName() {
        return driverName;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public LocationDTO getOrigin() {
        return origin;
    }

    public LocationDTO getDestination() {
        return destination;
    }

    public int getDriverRating() {
        return driverRating;
    }

    public int getVehicleRating() {
        return vehicleRating;
    }

    public List<String> getInconsistencyReports() {
        return inconsistencyReports;
    }

    public boolean isPetFriendly() {
        return petFriendly;
    }

    public boolean isBabyFriendly() {
        return babyFriendly;
    }
}