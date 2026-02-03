import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { RideOrderCreate } from "./models/ride-order-create";
import { RideOrderResponse } from "./models/ride-order-response";

export interface DriverRideDTO {
  id: number;
  originAddress: string;
  destinationAddress: string;
  scheduledTime: string;
  status: string;
}

export interface LocationDTO {
  latitude: number;
  longitude: number;
  address: string;
}

// Service for ordering a new ride - registered user
@Injectable({providedIn: 'root'})
export class RideOrderingService {

    constructor(private http: HttpClient) {}

    private apiUrl = 'http://localhost:8080/api/rides';

    // Create ride function
    createRide(data: RideOrderCreate): Observable<RideOrderResponse> {
        return this.http.post<RideOrderResponse>(`${this.apiUrl}/create-ride`, data);
    }

    // Report inconsistency for a ride
    reportInconsistency(rideId: number, reason: string): Observable<any> {
        return this.http.post(`${this.apiUrl}/${rideId}/inconsistency-report`, { reason });
    }

    finishRide(rideId: number): Observable<any> {
        return this.http.put(`http://localhost:8080/api/rides/${rideId}/finish`, {});
    }
    
    getScheduledRides(page: number, size: number): Observable<any> {
        return this.http.get(
          `${this.apiUrl}/driver/2?page=${page}&size=${size}`
        );
    }

    cancelRide(rideId: number, userId: number, reason: string, guest: boolean): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/cancel/${rideId}`,{userId, reason, guest});
    }

    stopRide(rideId: number, guest: boolean, stopLocation: LocationDTO): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/${rideId}/stop`,{ guest, stopLocation });
    }
}