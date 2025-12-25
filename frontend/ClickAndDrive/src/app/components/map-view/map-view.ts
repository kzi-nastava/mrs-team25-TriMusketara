import { Component, signal } from '@angular/core';
import { RidePopup } from '../../shared/ride-popup';

@Component({
  selector: 'app-map-view',
  standalone: true,
  imports: [], 
  templateUrl: './map-view.html',
  styleUrl: './map-view.css',
})
export class MapViewComponent {
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
}