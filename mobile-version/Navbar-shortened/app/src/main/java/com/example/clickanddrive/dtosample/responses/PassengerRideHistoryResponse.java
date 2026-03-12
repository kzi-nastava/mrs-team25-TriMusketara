package com.example.clickanddrive.dtosample.responses;

import com.example.clickanddrive.dtosample.LocationDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PassengerRideHistoryResponse {
    private Long id;
    private String startTime;
    private String endTime;
    private LocationDTO origin;
    private LocationDTO destination;
    private double totalPrice;
    private String driverEmail;
    private String status;

    // only for UI
    private boolean expanded = false;

    public Long getId() {
        return id;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public LocationDTO getOrigin() {
        return origin;
    }

    public LocationDTO getDestination() {
        return destination;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public String getStatus() {
        return status;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getFormattedStartTime() {
        try {
            LocalDateTime dt = LocalDateTime.parse(startTime);
            return dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm"));
        } catch (Exception e) {
            return startTime;
        }
    }

    public String getFormattedEndTime() {
        try {
            LocalDateTime dt = LocalDateTime.parse(endTime);
            return dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm"));
        } catch (Exception e) {
            return endTime;
        }
    }

    public String getFormattedDateRange() {
        return getFormattedStartTime() + " - " + getFormattedEndTime();
    }
}