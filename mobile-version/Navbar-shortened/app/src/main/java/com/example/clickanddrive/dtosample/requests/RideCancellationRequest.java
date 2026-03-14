package com.example.clickanddrive.dtosample.requests;

public class RideCancellationRequest {
    private Long userId;
    private String reason;
    private boolean guest;

    public RideCancellationRequest(Long userId, String reason, boolean guest) {
        this.userId = userId;
        this.reason = reason;
        this.guest = guest;
    }

    public Long getUserId() {
        return userId;
    }

    public String getReason() {
        return reason;
    }

    public boolean isGuest() {
        return guest;
    }
}