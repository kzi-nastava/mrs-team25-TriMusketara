import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { RideOrderCreate } from "./models/ride-order-create";

// Service for ordering a new ride - registered user
@Injectable({providedIn: 'root'})
export class RideOrderingService {

    constructor(private http: HttpClient) {}

    private apiUrl = 'http://localhost:8080/api/rides';

    // Create ride function
    createRide(data: RideOrderCreate): Observable<RideOrderCreate> {
        return this.http.post<RideOrderCreate>(`${this.apiUrl}/create-ride`, data);
    }

    // Report inconsistency for a ride
    reportInconsistency(rideId: number, reason: string): Observable<any> {
        return this.http.post(`${this.apiUrl}/${rideId}/inconsistency-report`, { reason });
    }
    
}