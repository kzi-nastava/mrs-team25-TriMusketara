package com.example.clickanddrive.dtosample.responses;

import com.example.clickanddrive.dtosample.LocationDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PassengerRideHistoryResponse {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
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

    // Inace su ove funkcije  vracale Stringove, jer su datumi bili String, sad su datum, ali jos uvek vracamo string da ne menjamo sad na 100 mesta, ako bude trebao datum napraviti nov getter
    public String getStartTime() {
        return startTime != null ? startTime.toString() : null;
    }

    public String getEndTime() {
        return endTime != null ? endTime.toString() : null;
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

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
    public String getFormattedStartTime() {
        return startTime != null ? startTime.format(FMT) : "-";
    }

    public String getFormattedEndTime() {
        return endTime != null ? endTime.format(FMT) : "-";
    }

    public String getFormattedDateRange() {
        return getFormattedStartTime() + " - " + getFormattedEndTime();
    }
}