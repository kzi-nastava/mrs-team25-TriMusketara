package com.example.clickanddrive.dtosample.requests;

public class InconsistencyReportRequest {
    private String reason;

    public InconsistencyReportRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}