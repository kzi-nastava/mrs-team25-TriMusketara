package com.example.clickanddrive.dtosample.responses;

import java.util.List;

public class ReportResponse {
    private List<DailyStats> dailyStats;
    private SummaryStats summary;

    public List<DailyStats> getDailyStats() { return dailyStats; }

    public SummaryStats getSummary() {
        return summary;
    }
}
