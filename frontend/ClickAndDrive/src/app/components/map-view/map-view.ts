import { Component, AfterViewInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

declare var mapboxgl: any;

@Component({
  selector: 'app-map-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './map-view.html',
  styleUrl: './map-view.css',
})
export class MapViewComponent implements AfterViewInit {

  @Input() size: 'large' | 'small' = 'large';

  ngAfterViewInit() {
    mapboxgl.accessToken = 'pk.eyJ1IjoicmliaWNuaWtvbGEiLCJhIjoiY21qbTJvNHFlMmV6OTNncXhpOGNiaTVnayJ9.Bhzo0Euk2D923K3smmoVaQ';

    new mapboxgl.Map({
      container: 'map',
      style: 'mapbox://styles/mapbox/dark-v11',
      center: [19.847781672927088, 45.23576711328475],
      zoom: 13
    });
  }
}
