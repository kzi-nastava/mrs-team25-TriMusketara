// This interface represents a response from backend when generating a report

export interface DailyStats {
    date: string;
    numberOfRides: number;
    totalKilometers: number;
    totalMoney: number;
}

export interface SummaryStats {
    totalRides: number;
    totalKilometers: number;
    totalMoney: number;
    avgRidesPerDay: number;
    avgKilometersPerDay: number;
    avgMoneyPerDay: number;
}

export interface ReportResponse {
    dailyStats: DailyStats[];
    summary: SummaryStats;
}
