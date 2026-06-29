package com.example.clickanddrive.dtosample.responses;

import com.example.clickanddrive.dtosample.LocationDTO;

import java.time.LocalDateTime;
import java.util.List;

public class PassengerRideDetailsResponse {
    private Long id;
    private Long routeId; // for adding to favorites
    private String driverEmail;
    private String driverName;
    private Double totalPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
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

    public Long getRouteId() {
        return routeId;
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
        return startTime != null ? startTime.toString() : null;
    }

    public String getEndTime() {
        return endTime != null ? endTime.toString() : null;
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