package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportResponseDTO {
    private List<DailyStatsDTO> dailyStats;
    private SummaryStatsDTO summary;
}
