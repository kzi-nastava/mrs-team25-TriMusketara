import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { DriverCreate } from "./models/driver-create";
import { Observable } from "rxjs";

@Injectable({providedIn: 'root'})
export class DriverService {

    constructor(private http: HttpClient) {}

    private apiUrl = 'http://localhost:8080/api/admin';

    // Register driver function
    registerDriver(data: DriverCreate): Observable<DriverCreate> {
        return this.http.post<DriverCreate>(`${this.apiUrl}/drivers`, data);
    }
}