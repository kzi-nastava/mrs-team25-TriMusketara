import { Component, signal, AfterViewInit } from '@angular/core';
import { RidePopup } from '../../shared/ride-popup';

declare var mapboxgl: any;

@Component({
  selector: 'app-map-view',
  standalone: true,
  imports: [], 
  templateUrl: './map-view.html',
  styleUrl: './map-view.css',
})
export class MapViewComponent implements AfterViewInit {
  constructor(public ridePopup: RidePopup) {}

  showRideData = signal(false);

  onShowRoute() {
    this.showRideData.set(true);
    this.ridePopup.close();
  }

  onOverlayClick() {
    this.showRideData.set(false);
    this.ridePopup.close();
  }

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