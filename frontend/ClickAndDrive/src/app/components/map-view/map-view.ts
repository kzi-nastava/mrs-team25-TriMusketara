import { Component, AfterViewInit, Input, ElementRef, ViewChild, Output, EventEmitter, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import mapboxgl from 'mapbox-gl';
import { VehicleService } from '../../services/vehicle.service';
import { ActiveVehicleResponse } from '../../services/models/active-vehicle-response';
import { Subscription } from 'rxjs/internal/Subscription';
import { switchMap } from 'rxjs/internal/operators/switchMap';
import { startWith } from 'rxjs/internal/operators/startWith';
import { interval } from 'rxjs/internal/observable/interval';

@Component({
  selector: 'app-map-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './map-view.html',
  styleUrls: ['./map-view.css'],
})
export class MapViewComponent implements AfterViewInit, OnDestroy {

  @Input() size: 'large' | 'small' = 'large';
  //@Output() etaCalculated = new EventEmitter<number>();
  @Output() routeCalculated = new EventEmitter<{durationMinutes: number, distanceKm: number}>(); // Send to front both duration and distance for given route

  @ViewChild('mapContainer', { static: true }) mapContainer!: ElementRef<HTMLDivElement>;

  public map!: mapboxgl.Map;
  private activeMarkers: mapboxgl.Marker[] = [];
  private vehicleSub?: Subscription;

  constructor(
    private http: HttpClient, 
    private vehicleService: VehicleService
  ) {}

  ngAfterViewInit() {
    mapboxgl.accessToken = 'pk.eyJ1IjoicmliaWNuaWtvbGEiLCJhIjoiY21qbTJvNHFlMmV6OTNncXhpOGNiaTVnayJ9.Bhzo0Euk2D923K3smmoVaQ';

    this.map = new mapboxgl.Map({
      container: this.mapContainer.nativeElement,
      style: 'mapbox://styles/mapbox/dark-v11',
      center: [19.84234796637571, 45.25430134740514],
      zoom: 13
    });

    this.map.on('load', () => {
      this.startVehiclePolling();
    });
  }

   // Function that fetches new data every 5 seconds
  private startVehiclePolling() {
    this.vehicleSub = interval(5000) // 5 seconds
      .pipe(
        startWith(0), // Right away on start
        switchMap(() => this.vehicleService.getActiveVehicles())
      )
      .subscribe({
        next: (vehicles) => this.addTaxiMarkers(vehicles),
        error: (err) => console.error('Polling error:', err)
      });
  }

  
  ngOnDestroy() {
    this.vehicleSub?.unsubscribe();
  }

   private loadActiveVehicles() {
    this.vehicleService.getActiveVehicles().subscribe({
      next: (vehicles: ActiveVehicleResponse[]) => {
        this.addTaxiMarkers(vehicles);
      },
      error: (err) => console.error('Greška pri učitavanju vozila:', err)
    });
  }

  private addTaxiMarkers(vehicles: ActiveVehicleResponse[]) {
    // Refresh markers
    this.activeMarkers.forEach(m => m.remove());
    this.activeMarkers = [];

    //console.log('Dodavanje markera za vozila:', vehicles.length);

    vehicles.forEach((v) => {
      const el = document.createElement('img');
      el.className = 'taxi-marker';
      el.style.width = '45px';
      el.style.height = '45px';

      //console.log(`Vozilo ${v.id} na lokaciji: [${v.currentLocation.longitude}, ${v.currentLocation.latitude}]`);
      
      // Later on: differentiate busy/free vehicles with different icons
      el.src = v.busy ? '/taxi-icon.png' : '/taxi-icon.png';
      if (!v.busy) el.src = '/taxi-icon.png';
      const marker = new mapboxgl.Marker(el)
      // IMPORTANT: Mapbox uses different coordinate order, we've mistaken long and lat so now it's reversed, lat is first
      if(v.busy){
        
        marker.setLngLat([v.currentLocation.latitude, v.currentLocation.longitude])
        marker.setPopup(new mapboxgl.Popup().setHTML(`<b>ZAUZETO Vozilo #${v.id}</b><br>${v.currentLocation.address}`))
        marker.addTo(this.map);
      }
      else{
        marker.setLngLat([v.currentLocation.latitude, v.currentLocation.longitude])
        marker.setPopup(new mapboxgl.Popup().setHTML(`<b>Vozilo #${v.id}</b><br>${v.currentLocation.address}`))
        marker.addTo(this.map);
      }
      

      this.activeMarkers.push(marker);
    });
  }

  drawRouteAndCalculateETA(origin: [number, number], destination: [number, number]) {
    const url = `https://api.mapbox.com/directions/v5/mapbox/driving/` +
      `${origin[0]},${origin[1]};${destination[0]},${destination[1]}` +
      `?geometries=geojson&overview=full&access_token=${mapboxgl.accessToken}`;

    this.http.get<any>(url).subscribe((res) => {
      if (!res.routes || !res.routes.length) return;

      const route = res.routes[0].geometry;
      const durationSeconds = res.routes[0].duration; // s
      const distanceMeters = res.routes[0].distance;  // m
      //const etaMinutes = Math.round(durationSeconds / 60);
      const durationMinutes = Math.round(durationSeconds / 60);
      const distanceKm = Math.round((distanceMeters/1000) * 100) / 100; 

      // Sending both to backend
      console.log(' Emitting from map:', { durationMinutes, distanceKm }); 
      this.routeCalculated.emit({durationMinutes, distanceKm});
      //this.etaCalculated.emit(etaMinutes);

      if (this.map.getSource('route')) {
        this.map.removeLayer('route');
        this.map.removeSource('route');
      }

      this.map.addSource('route', {
        type: 'geojson',
        data: { type: 'Feature', geometry: route, properties: {} }
      });

      this.map.addLayer({
        id: 'route',
        type: 'line',
        source: 'route',
        layout: { 'line-join': 'round', 'line-cap': 'round' },
        paint: { 'line-color': '#F5CB5C', 'line-width': 5 }
      });

      const bounds = new mapboxgl.LngLatBounds();
      for (const coord of route.coordinates) {
        bounds.extend(coord as [number, number]);
      }
      this.map.fitBounds(bounds, { padding: 60, duration: 500 });
    }, (err) => console.error('Directions API error:', err));
  }

  // Draws a route 
  drawRouteWithStops(coordinates: [number, number][]) {
    if (coordinates.length < 2) return;

    const coordsString = coordinates 
    .map(c => `${c[0]},${c[1]}`)
    .join(';');

    const url =
    `https://api.mapbox.com/directions/v5/mapbox/driving/` +
    `${coordsString}` +
    `?geometries=geojson&overview=full&access_token=${mapboxgl.accessToken}`;
  
    this.http.get<any>(url).subscribe(res => {
      if (!res.routes || !res.routes.length) return;

      const route = res.routes[0].geometry;
      const durationSeconds = res.routes[0].duration;
      const distanceMeters = res.routes[0].distance;
      //const etaMinutes = Math.round(durationSeconds / 60);
      const durationMinutes = Math.round(durationSeconds / 60);
      const distanceKm = Math.round((distanceMeters / 1000) * 100) / 100;

      // Emit
      console.log('Emitting from map:', { durationMinutes, distanceKm }); 
      this.routeCalculated.emit({durationMinutes, distanceKm});
      //this.etaCalculated.emit(etaMinutes);

      // Clear old route if exists
      if (this.map.getSource('route')) {
        this.map.removeLayer('route');
        this.map.removeSource('route');
      }

      // Add new route
      this.map.addSource('route', {
        type: 'geojson', 
        data: {
          type: 'Feature',
          geometry: route, 
          properties: {}
        }
      });

      this.map.addLayer({
        id: 'route',
        type: 'line',
        source: 'route',
        layout: {
          'line-join': 'round',
          'line-cap': 'round'
        },
        paint: {
          'line-color': '#F5CB5C',
          'line-width': 5
        }
      });


      // Add markers for additional stops (not to origin and destination)
      const stops = coordinates.slice(1, -1);
      stops.forEach(coords => {
        const stopsMarker = document.createElement('div');
        stopsMarker.className = 'stop-marker';
        stopsMarker.innerHTML = `
            <svg width="26px" height="26px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M8 18L10.29 20.29C10.514 20.5156 10.7804 20.6946 11.0739 20.8168C11.3674 20.9389 11.6821 21.0018 12 21.0018C12.3179 21.0018 12.6326 20.9389 12.9261 20.8168C13.2196 20.6946 13.486 20.5156 13.71 20.29L16 18H18C19.0609 18 20.0783 17.5786 20.8284 16.8285C21.5786 16.0783 22 15.0609 22 14V7C22 5.93913 21.5786 4.92178 20.8284 4.17163C20.0783 3.42149 19.0609 3 18 3H6C4.93913 3 3.92172 3.42149 3.17157 4.17163C2.42142 4.92178 2 5.93913 2 7V14C2 15.0609 2.42142 16.0783 3.17157 16.8285C3.92172 17.5786 4.93913 18 6 18H8Z" stroke="#CFDBD5" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
        `;
        new mapboxgl.Marker(stopsMarker).setLngLat(coords).addTo(this.map);
      });

      // Add marker to destination coords
      const destinationCoords = coordinates[coordinates.length - 1];
      const destMarker = document.createElement('div');
      destMarker.className = 'destination-marker';
      destMarker.innerHTML = `
          <svg width="26px" height= "26px" viewBox="0 0 23 33" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M22.5 11.7C22.5 21.3 11.5 32.5 11.5 32.5C11.5 32.5 0.5 21.3 0.5 11.7C0.5 5.508 5.41857 0.5 11.5 0.5C17.5814 0.5 22.5 5.508 22.5 11.7Z" stroke="#F55C5C" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M11.5001 18.1C14.9716 18.1 17.7858 15.2346 17.7858 11.7C17.7858 8.16538 14.9716 5.3 11.5001 5.3C8.02857 5.3 5.21436 8.16538 5.21436 11.7C5.21436 15.2346 8.02857 18.1 11.5001 18.1Z" stroke-width="2.5" stroke="#F55C5C" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
      `;
      new mapboxgl.Marker(destMarker, {anchor: 'bottom'}).setLngLat(destinationCoords).addTo(this.map);


      const bounds = new mapboxgl.LngLatBounds();
      route.coordinates.forEach((c: [number, number]) => bounds.extend(c));

      this.map.fitBounds(bounds, {
        padding: 60,
        duration: 500
      });
    });
  }
}

