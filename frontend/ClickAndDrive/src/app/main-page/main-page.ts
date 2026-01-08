import { Component, signal } from '@angular/core';
import { MapViewComponent } from '../components/map-view/map-view';
import { RouterOutlet } from '@angular/router';
import { RidePopup } from '../shared/ride-popup';

@Component({
  selector: 'app-main-page',
  standalone: true,
  imports: [MapViewComponent, RouterOutlet], 
  templateUrl: './main-page.html',
  styleUrl: './main-page.css'
})
export class MainPageComponent {
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