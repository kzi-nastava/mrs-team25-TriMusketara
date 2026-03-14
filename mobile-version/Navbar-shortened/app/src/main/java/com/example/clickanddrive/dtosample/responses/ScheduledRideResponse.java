package com.example.clickanddrive.dtosample.responses;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScheduledRideResponse implements Serializable {
    private Long id;
    private String origin;
    private String destination;
    private String scheduledTime;
    private boolean guest;

    public Long getId() {
        return id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public boolean isGuest() {
        return guest;
    }

    public String getFormattedScheduledTime() {
        try {
            LocalDateTime dt = LocalDateTime.parse(scheduledTime);
            return dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm"));
        } catch (Exception e) {
            return scheduledTime;
        }
    }
}