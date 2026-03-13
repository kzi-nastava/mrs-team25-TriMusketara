package com.example.clickanddrive.dtosample.responses;

import com.example.clickanddrive.dtosample.LocationDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminRideHistoryResponse {
    private Long id;

    private String startTime;
    private String endTime;

    private LocationDTO origin;
    private LocationDTO destination;

    private double totalPrice;

    private boolean panicPressed;

    private boolean cancelled;
    private String cancelledBy;

    private String driverEmail;
    private List<String> passengerEmails;

    private String status;

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

    public boolean isPanicPressed() {
        return panicPressed;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public List<String> getPassengerEmails() {
        return passengerEmails;
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
        return formatDate(startTime);
    }

    public String getFormattedEndTime() {
        return formatDate(endTime);
    }

    public String getFormattedDateRange() {
        return getFormattedStartTime() + " - " + getFormattedEndTime();
    }

    private String formatDate(String raw) {
        try {
            LocalDateTime dt = LocalDateTime.parse(raw);
            return dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm"));
        } catch (Exception e) {
            return raw;
        }
    }
}