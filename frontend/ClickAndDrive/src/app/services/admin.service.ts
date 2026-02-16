import { HttpClient } from "@angular/common/http";
import { NoteRequest, UserProfileInformation } from "./models/user-profile-information";
import { Observable } from "rxjs";
import { Injectable } from "@angular/core";

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
}