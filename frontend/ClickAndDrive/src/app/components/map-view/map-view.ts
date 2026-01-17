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
}
