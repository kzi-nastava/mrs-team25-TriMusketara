import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { EmptyError, Observable } from 'rxjs';
import { RouteFromFavorites } from './models/route-from-favorites';

@Injectable({
    providedIn: 'root'
})
export class PassengerService {
    private apiUrl = 'http://localhost:8080/api/passenger';

    constructor(
        private http: HttpClient
    ) {}

    // GET users favorite routes
    getFavoriteRoutes(passengerId: number): Observable<RouteFromFavorites[]> {
        return this.http.get<RouteFromFavorites[]>(`${this.apiUrl}/${passengerId}/favorite-routes`);
    }

    // Remove a route from favorites
    removeFavoriteRoute(passengerId: number, routeId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${passengerId}/${routeId}/remove-route`);
    }
}
