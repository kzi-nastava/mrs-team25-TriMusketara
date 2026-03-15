package com.example.clickanddrive.dtosample.responses;

public class PanicResponse {
    private Long id;
    private Long rideId;
    private boolean guest;
    private String triggeredByName;
    private String triggeredByEmail;
    private String createdAt;
    private boolean resolved;
    private String originAddress;
    private String destinationAddress;

    public Long getId() {
        return id;
    }

    public Long getRideId() {
        return rideId;
    }

    public boolean isGuest() {
        return guest;
    }

    public String getTriggeredByName() {
        return triggeredByName;
    }

    public String getTriggeredByEmail() {
        return triggeredByEmail;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isResolved() {
        return resolved;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }
}