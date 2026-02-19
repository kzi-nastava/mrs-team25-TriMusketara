import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { EmptyError, Observable } from 'rxjs';
import { RouteFromFavorites } from './models/route-from-favorites';
import { PassengerRideHistory } from './models/passenger-ride-history';
import { PageResponse } from './models/page-response';

@Injectable({
    providedIn: 'root'
})
export class PassengerService {
    private apiUrl = 'http://localhost:8080/api/passenger';

    constructor(
        private http: HttpClient
    ) {}

    // GET users favorite routes
    getFavoriteRoutes(passengerId: number, page: number, size: number): Observable<PageResponse<RouteFromFavorites>> {
        return this.http.get<PageResponse<RouteFromFavorites>>(`${this.apiUrl}/${passengerId}/favorite-routes?page=${page}&${size}`);
    }

    // Remove a route from favorites
    removeFavoriteRoute(passengerId: number, routeId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${passengerId}/${routeId}/remove-route`);
    }

    getPassengerHistory(passengerId: number) {
        return this.http.get<PassengerRideHistory[]>(
            `http://localhost:8080/api/passenger/${passengerId}/ride-history`
        );
    }
}
