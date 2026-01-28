// This service will be used for all user interactions
// Get a user, change his informations, change password...

import { HttpClient } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { UserProfileInformation } from "./models/user-profile-information";
import { Observable } from "rxjs";
import { VehicleInformation } from "./models/driver-vehicle-information";
import { ChangePasswordRequest } from "./models/change-password";


@Injectable({providedIn: 'root'})
export class UserService {

    constructor(private http: HttpClient) {}

    private apiUrlUser = 'http://localhost:8080/api/user';
    private apiUrlDriver = 'http://localhost:8080/api/drivers';

    // Get logged in users information for display and change
    getUserProfileInfo(userId: number): Observable<UserProfileInformation> {
        return this.http.get<UserProfileInformation>(`${this.apiUrlUser}/${userId}/profile`);
    }

    // Get vehicle information for driver
    getDriverVehicle(userId: number): Observable<VehicleInformation> {
        return this.http.get<VehicleInformation>(`${this.apiUrlDriver}/${userId}/vehicle`);
    }

    // Save changed information (if driver + vehicle)
    changeUserInfo(userId: number, updatedProfile: UserProfileInformation): Observable<UserProfileInformation> {
        return this.http.put<UserProfileInformation>(`${this.apiUrlUser}/${userId}/profile-update`, updatedProfile);
    }

    // Change user password
    changePassword(request: ChangePasswordRequest): Observable<any> {
        return this.http.post(`${this.apiUrlUser}/change-password`, request);
    }
}