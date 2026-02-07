package com.example.clickanddrive.dtosample.responses;

import com.example.clickanddrive.dtosample.LocationDTO;

import java.util.List;

public class DriverRideHistoryResponse {
    private Long id;
    private String startTime;
    private String endTime;
    private LocationDTO origin;
    private LocationDTO destination;
    private double totalPrice;
    private List<String> passengerEmails;
    private boolean panicPressed;

    // needed for UI, not from backend
    private boolean expanded = false;

    // Getters/setters
    public Long getId() { return id; }
    public String getStartTime() { return startTime; }
    public LocationDTO getOrigin() { return origin; }
    public LocationDTO getDestination() { return destination; }
    public double getTotalPrice() { return totalPrice; }
    public List<String> getPassengerEmails() { return passengerEmails; }
    public boolean isPanicPressed() { return panicPressed; }
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }

    // Helper for date formatting
    public String getFormattedDate() {
        // ISO format "yyyy-MM-dd'T'HH:mm:ss"
        try {
            java.time.LocalDateTime dt = java.time.LocalDateTime.parse(startTime);
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
            return dt.format(formatter);
        } catch (Exception e) {
            return startTime;
        }
    }
}
