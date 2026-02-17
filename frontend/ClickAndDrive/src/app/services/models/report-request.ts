// Interface represent report request dto from backend when we want to generate a report

export interface ReportRequest {
    dateFrom: string;
    dateTo: string;
    userId?: number;
    userType?: 'DRIVER' | 'PASSENGER' | 'ALL_DRIVERS' | 'ALL_PASSENGERS';
}
