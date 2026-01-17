import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, map, switchMap, throwError } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class Map {

  private token = 'pk.eyJ1IjoicmliaWNuaWtvbGEiLCJhIjoiY21qbTJvNHFlMmV6OTNncXhpOGNiaTVnayJ9.Bhzo0Euk2D923K3smmoVaQ';
  etaMinutes: number | null = null;

  private bbox = '19.78,45.22,19.92,45.30';

  constructor(private http: HttpClient) {}

  getRouteByAddress(origin: string, destination: string): Observable<any> {

    const originUrl =
      `https://api.mapbox.com/geocoding/v5/mapbox.places/${encodeURIComponent(origin)}.json` +
      `?bbox=${this.bbox}&types=address&limit=1&access_token=${this.token}`;

    const destinationUrl =
      `https://api.mapbox.com/geocoding/v5/mapbox.places/${encodeURIComponent(destination)}.json` +
      `?bbox=${this.bbox}&types=address&limit=1&access_token=${this.token}`;

    return forkJoin({
      origin: this.http.get<any>(originUrl),
      destination: this.http.get<any>(destinationUrl)
    }).pipe(
      switchMap(({ origin, destination }) => {

        console.log('ORIGIN GEOCODE:', origin.features[0].center[0], origin.features[0].center[1]);
        console.log('DESTINATION GEOCODE:', destination.features[0].center[0], destination.features[0].center[1]);

        if (!origin.features.length || !destination.features.length) {
          throw new Error('Geocoding nije vratio rezultate');
        }

        const o = origin.features[0].center;
        const d = destination.features[0].center;

        const directionsUrl =
          `https://api.mapbox.com/directions/v5/mapbox/driving/` +
          `${o[0]},${o[1]};${d[0]},${d[1]}` +
          `?geometries=geojson&overview=full&access_token=${this.token}`;

        return this.http.get(directionsUrl);
      })
    );
  }

  getRouteAndETA(
    origin: [number, number],
    destination: [number, number]
  ) {
    const url =
    `https://api.mapbox.com/directions/v5/mapbox/driving/` +
    `${origin[0]},${origin[1]};${destination[0]},${destination[1]}` +
    `?geometries=geojson&overview=full&access_token=${this.token}`;

    this.http.get<any>(url).subscribe(res => {
      const route = res.routes[0];

      const durationSeconds = route.duration;
      const distanceMeters = route.distance;

      this.etaMinutes = Math.round(durationSeconds / 60);
    });
  }
}
