import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { DriverCreate } from "./models/driver-create";
import { Observable } from "rxjs";
import { CompleteRegistration } from "./models/complete-driver-registration";
import { Driver } from "./models/driver-reg-response";
import { DriverRideHistory } from "./models/driver-history";

@Injectable({providedIn: 'root'})
export class DriverService {

    constructor(private http: HttpClient) {}

    private adminUrl = 'http://localhost:8080/api/admin';
    private driverUrl = "http://localhost:8080/api/drivers";

    // Register driver function
    registerDriver(data: DriverCreate): Observable<DriverCreate> {
        return this.http.post<DriverCreate>(`${this.adminUrl}/drivers`, data);
    }

    // Complete driver registration
    completeRegistration(request: CompleteRegistration): Observable<string> {
        const headers = new HttpHeaders({ 'skip': 'true' }); // do not send token 
        return this.http.post(`${this.driverUrl}/complete-registration`, request, { headers, responseType: 'text'});
    }

    // Get driver ride history
    getDriverHistory(driverId: number): Observable<DriverRideHistory[]> {
        return this.http.get<DriverRideHistory[]>(`${this.driverUrl}/${driverId}/ride-history`);
    }
}