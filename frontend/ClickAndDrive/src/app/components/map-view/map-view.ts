import { Component, AfterViewInit, Input, ElementRef, ViewChild, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import mapboxgl from 'mapbox-gl';

@Component({
  selector: 'app-map-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './map-view.html',
  styleUrls: ['./map-view.css'],
})
export class MapViewComponent implements AfterViewInit {

  @Input() size: 'large' | 'small' = 'large';
  @Output() etaCalculated = new EventEmitter<number>();

  @ViewChild('mapContainer', { static: true }) mapContainer!: ElementRef<HTMLDivElement>;

  public map!: mapboxgl.Map;

  constructor(private http: HttpClient) {}

  ngAfterViewInit() {
    mapboxgl.accessToken = 'pk.eyJ1IjoicmliaWNuaWtvbGEiLCJhIjoiY21qbTJvNHFlMmV6OTNncXhpOGNiaTVnayJ9.Bhzo0Euk2D923K3smmoVaQ';

    this.map = new mapboxgl.Map({
      container: this.mapContainer.nativeElement,
      style: 'mapbox://styles/mapbox/dark-v11',
      center: [19.84234796637571, 45.25430134740514],
      zoom: 13
    });

    this.map.on('load', () => {
      this.addTaxiMarkers();
    });
  }

  private addTaxiMarkers() {

  const taxiLocations: [number, number][] = [
    [19.830221442332125, 45.26411740426349],
    [19.882080282032774, 45.242658480042856],
    [19.82537097950451, 45.239920691629045],
  ];

  taxiLocations.forEach((coords) => {
    const el = document.createElement('img');
    el.className = 'taxi-marker';
    
    el.style.width = '45px';
    el.style.height = '45px';
    el.src = '/taxi-icon.png';

    new mapboxgl.Marker(el)
      .setLngLat(coords)
      .addTo(this.map);
  });
  }

  drawRouteAndCalculateETA(origin: [number, number], destination: [number, number]) {
    const url = `https://api.mapbox.com/directions/v5/mapbox/driving/` +
      `${origin[0]},${origin[1]};${destination[0]},${destination[1]}` +
      `?geometries=geojson&overview=full&access_token=${mapboxgl.accessToken}`;

    this.http.get<any>(url).subscribe((res) => {
      if (!res.routes || !res.routes.length) return;

      const route = res.routes[0].geometry;
      const durationSeconds = res.routes[0].duration;
      const etaMinutes = Math.round(durationSeconds / 60);

      this.etaCalculated.emit(etaMinutes);

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
      const etaMinutes = Math.round(durationSeconds / 60);

      // Emit
      this.etaCalculated.emit(etaMinutes);

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

