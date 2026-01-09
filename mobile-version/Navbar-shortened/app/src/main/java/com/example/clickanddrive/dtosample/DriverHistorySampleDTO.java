package com.example.clickanddrive.dtosample;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DriverHistorySampleDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String departureAddress; // Simpler for CT1
    private String destinationAddress;
    private double totalPrice;
    private boolean panicPressed;


    public DriverHistorySampleDTO(Long id, LocalDateTime startTime, String departureAddress, String destinationAddress, double totalPrice, boolean panicPressed) {
        this.id = id;
        this.startTime = startTime;
        this.departureAddress = departureAddress;
        this.destinationAddress = destinationAddress;
        this.totalPrice = totalPrice;
        this.panicPressed = panicPressed;
    }

    // Getters
    public LocalDateTime getStartTime(){
        return startTime;
    }
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
        return startTime.format(formatter);
    }
    public String getDepartureAddress() { return departureAddress; }
    public String getDestinationAddress() { return destinationAddress; }
    public double getTotalPrice() { return totalPrice; }
    public boolean isPanicPressed() { return panicPressed; }
}