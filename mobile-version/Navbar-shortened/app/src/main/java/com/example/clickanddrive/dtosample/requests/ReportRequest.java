package com.example.clickanddrive.dtosample.requests;

import java.time.LocalDateTime;

public class ReportRequest {
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private Long userId;
    private String userType; // DRIVER, PASSENGER, ALL_DRIVERS, ALL_PASSENGERS


    public ReportRequest(LocalDateTime dateFrom, LocalDateTime dateTo, Long userId, String userType) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.userId = userId;
        this.userType = userType;
    }

    public LocalDateTime getDateFrom() { return dateFrom; }
    public LocalDateTime getDateTo() { return dateTo; }

    public Long getUserId() { return userId; }
    public String getUserType() { return userType; }
}
