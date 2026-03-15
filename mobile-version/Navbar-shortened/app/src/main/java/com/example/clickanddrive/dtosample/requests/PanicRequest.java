package com.example.clickanddrive.dtosample.requests;

public class PanicRequest {
    private Long rideId;
    private boolean guest;
    private Long userId;

    public PanicRequest(Long rideId, boolean guest, Long userId) {
        this.rideId = rideId;
        this.guest = guest;
        this.userId = userId;
    }

    public Long getRideId() {
        return rideId;
    }

    public boolean isGuest() {
        return guest;
    }

    public Long getUserId() {
        return userId;
    }
}