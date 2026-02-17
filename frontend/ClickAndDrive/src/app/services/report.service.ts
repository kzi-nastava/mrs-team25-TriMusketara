// Report service
import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ReportRequest } from "./models/report-request";
import { ReportResponse } from "./models/report-response";
import { Observable } from "rxjs";


@Injectable({
    providedIn: 'root'
})
export class ReportService {
    private apiUrl = 'http://localhost:8080/api/reports';

    constructor(private http: HttpClient) {}

    // Generate a report function
    generateReport(request: ReportRequest): Observable<ReportResponse> {
        return this.http.post<ReportResponse>(`${this.apiUrl}/generate`, request);
    }
}
