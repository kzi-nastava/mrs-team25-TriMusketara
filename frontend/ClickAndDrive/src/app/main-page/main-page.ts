import { Component, signal, ViewChild } from '@angular/core';
import { MapViewComponent } from '../components/map-view/map-view';
import { RouterOutlet } from '@angular/router';
import { RidePopup } from '../shared/ride-popup';
import { RideOrdering } from '../layout/ride-ordering/ride-ordering';
import { AuthService } from '../services/auth.service';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-main-page',
  standalone: true,
  imports: [MapViewComponent, RouterOutlet, RideOrdering, FormsModule],
  templateUrl: './main-page.html',
  styleUrls: ['./main-page.css']
})
export class MainPageComponent {
  constructor(public ridePopup: RidePopup, public auth: AuthService) {}

  @ViewChild('mapView') mapView!: MapViewComponent;

  originAddress = '';
  destinationAddress = '';
  etaMinutes: number | null = null;

  showRideData = signal(false);

  async onShowRoute() {
    if (!this.originAddress || !this.destinationAddress) return;

    try {
      const originCoords = await this.geocodeAddress(this.originAddress);
      const destCoords = await this.geocodeAddress(this.destinationAddress);

      if (this.mapView) {
        this.mapView.drawRouteAndCalculateETA(originCoords, destCoords);
      }

      this.showRideData.set(true);
      this.ridePopup.close();
    } catch (err) {
      console.error('Geocoding error:', err);
      alert('Address not found: ' + err);
    }
  }

  onOverlayClick() {
    this.ridePopup.close();
  }

  private async geocodeAddress(address: string): Promise<[number, number]> {
    const token = 'pk.eyJ1IjoicmliaWNuaWtvbGEiLCJhIjoiY21qbTJvNHFlMmV6OTNncXhpOGNiaTVnayJ9.Bhzo0Euk2D923K3smmoVaQ';
    const bbox = [19.75, 45.20, 19.95, 45.30];
    const url = `https://api.mapbox.com/geocoding/v5/mapbox.places/${encodeURIComponent(address)}.json` + `?access_token=${token}&limit=1&bbox=${bbox.join(',')}`;

    const response = await fetch(url);
    const data = await response.json();

    if (!data.features || !data.features.length) {
      throw new Error(address);
    }

    return data.features[0].center as [number, number];
  }

  onEtaReceived(minutes: number) {
    this.etaMinutes = minutes;
  }
}
