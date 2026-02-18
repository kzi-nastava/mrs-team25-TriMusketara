import { HttpClient } from "@angular/common/http";
import { NoteRequest, UserProfileInformation } from "./models/user-profile-information";
import { Observable } from "rxjs";
import { Injectable } from "@angular/core";
import { AdminRideHistory } from "./models/admin-ride-history";
import { AdminUser } from "./models/admin-user";
import { VehiclePrice } from "./models/vehicle-price";

@Injectable({
    providedIn: 'root'
})
export class AdminService {
    private apiUrl = 'http://localhost:8080/api/admin';

    constructor(private http: HttpClient) {}

    // GET all drivers
    getAllDrivers(): Observable<UserProfileInformation[]> {
        return this.http.get<UserProfileInformation[]>(`${this.apiUrl}/drivers/all`);
    }

    // GET all passengers
    getAllPassengers(): Observable<UserProfileInformation[]> {
        return this.http.get<UserProfileInformation[]>(`${this.apiUrl}/passengers/all`);
    }

    // Block a user
    blockUser(userId: number, reason: string): Observable<UserProfileInformation> {
        const body: NoteRequest = {message: reason};
        return this.http.put<UserProfileInformation>(`${this.apiUrl}/users/${userId}/block`, body);
    }

    // Unblock a user
    unblockUser(userId: number): Observable<UserProfileInformation> {
        return this.http.put<UserProfileInformation>(`${this.apiUrl}/users/${userId}/unblock`, {});
    }

    // Leave a note
    leaveNote(userId: number, note: string): Observable<UserProfileInformation> {
        const body: NoteRequest = {message: note}
        return this.http.put<UserProfileInformation>(`${this.apiUrl}/users/${userId}/note`, body);
    }

    getRideHistory(id: number, role: string, from?: string, to?: string, sortBy?: string) {

        let params: any = {
            id: id,
            role: role,
            sortBy: sortBy || 'date'
        };

        if (from) params.from = from;
        if (to) params.to = to;

        return this.http.get<AdminRideHistory[]>(
            'http://localhost:8080/api/admin/ride-history',
            { params }
        );
    }

    getAllUsers() {
        return this.http.get<AdminUser[]>('http://localhost:8080/api/admin/users');
    // Get current prices
    getPrices(): Observable<VehiclePrice> {
        return this.http.get<VehiclePrice>(`${this.apiUrl}/prices`);
    }

    // Update prices
    updatePrices(prices: VehiclePrice): Observable<any> {
        return this.http.put<any>(`${this.apiUrl}/prices`, prices);
    }
}