import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RouteFromFavorites } from './models/route-from-favorites';

@Injectable({
    providedIn: 'root'
})
export class PassengerService {
    private apiUrl = 'http://localhost:8080/api/passenger';

    constructor(
        private http: HttpClient
    ) {}

    getFavoriteRoutes(passengerId: number): Observable<RouteFromFavorites[]> {
        return this.http.get<RouteFromFavorites[]>(`${this.apiUrl}/${passengerId}/favorite-routes`);
    }
}