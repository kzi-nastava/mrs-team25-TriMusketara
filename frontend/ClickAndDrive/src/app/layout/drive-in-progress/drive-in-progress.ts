import { Component, inject, signal, ViewChild, AfterViewInit } from '@angular/core';
import { MapViewComponent } from '../../components/map-view/map-view';
import { RouterOutlet, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Map } from '../../services/map';
import { RideOrderingService } from '../../services/ride.service';
import { map } from 'rxjs';

@Component({
  selector: 'app-drive-in-progress',
  standalone: true,
  imports: [RouterOutlet, MapViewComponent],
  templateUrl: './drive-in-progress.html',
  styleUrl: './drive-in-progress.css',
})
export class DriveInProgress implements AfterViewInit {
  auth = inject(AuthService);
  router = inject(Router);
  private mapService = inject(Map);
  private rideService = inject(RideOrderingService);

  @ViewChild(MapViewComponent) mapView!: MapViewComponent;

  showFinishNotification = signal(false);
  activeRide: any = null; // Vožnja koja je trenutno aktivna

  async ngAfterViewInit() {
    const rideData = localStorage.getItem('activeRideData');
    if (!rideData) {
      alert('No active ride found. Returning to scheduled rides.');
      this.router.navigate(['/scheduled-rides']);
      return;
    }

    this.activeRide = JSON.parse(rideData);

    this.auth.setRideData(this.activeRide.origin, this.activeRide.destination);
    this.auth.setInDrive(true);

    if (this.auth.origin() && this.auth.destination()) {
      await this.drawRouteOnLoad();
    }
  }

  private async drawRouteOnLoad() {
    try {
      console.log('Drawing route for:', this.auth.origin(), this.auth.destination());
      const originCoords = await this.mapService.geocodeAddress(this.auth.origin());
      const destCoords = await this.mapService.geocodeAddress(this.auth.destination());

      setTimeout(() => {
        if (this.mapView && this.mapView.map) {
          this.mapView.drawRouteAndCalculateETA(originCoords, destCoords);
        }
      }, 200);
    } catch (err) {
      console.error('Failed to draw route on load:', err);
    }
  }

  onEtaReceived(minutes: number) {
    this.auth.eta.set(minutes);
  }

  onRouteCalculated(info: { durationMinutes: number; distanceKm: number }) {
    this.auth.eta.set(info.durationMinutes);
    console.log('Route info:', info);
  }

  // ---------------- Passenger actions ----------------
  onFinishPassenger() {
    this.auth.setInDrive(false);

    // 2. Show the post-ride notification overlay
    this.showFinishNotification.set(true);
  }

  openRating() {
    this.showFinishNotification.set(false);
    this.router.navigate(['/rate-ride']);
  }

  // Method to return to home/order screen
  resetToOrder() {
    this.showFinishNotification.set(false);
    this.router.navigate(['/map']);
  }

  // ---------------- Driver actions ----------------

  onStopDriver() {
    const rideDataStr = localStorage.getItem('activeRideData');
    if (!rideDataStr) {
      alert('No active ride');
      return;
    }

    const ride = JSON.parse(rideDataStr);

    const stopLocation = {
      latitude: 45.2671,
      longitude: 19.8335,
      address: 'Stopped at current location'
    };

    this.rideService.stopRide(ride.id, ride.guest, stopLocation)
      .subscribe({
        next: () => {
          localStorage.removeItem('activeRideData');
          this.auth.setInDrive(false);
          this.router.navigate(['/map']);
          alert('Stopped at current location.');
        },
        error: err => {
          console.error(err);
          alert('Failed to stop ride');
        }
      });
    
    }

  // onFinishDriver() {
  //   if (!this.activeRide) return;
  // calcDistance(origin: string, destination: string): number {
  //   const coords1 = this.mapService.geocodeAddress(origin);
  //   const coords2 = this.mapService.geocodeAddress(destination);

  //   if (!coords1 || !coords2) {
  //     console.error('Failed to geocode addresses for distance calculation');
  //     return 0;
  //   }


  // }

  async onFinishDriver() {
  if (!this.activeRide) return;

  try {
    // 1. Prvo geokodiramo adrese da dobijemo sveže koordinate
    const originCoords = await this.mapService.geocodeAddress(this.activeRide.origin);
    const destCoords = await this.mapService.geocodeAddress(this.activeRide.destination);

    // 2. Tražimo distancu od Mapboxa neposredno pre slanja
    this.mapService.getRouteDistanceOnly(originCoords, destCoords).subscribe({
      next: (km) => {
        console.log('Finalna distanca za slanje:', km);
        
        // 3. Šaljemo na backend
        this.rideService.finishRide(this.activeRide.id, km).subscribe({
          next: () => this.completeRideFlow(),
          error: (err) => {
            console.error('Greška pri završetku:', err);
            this.completeRideFlow();
          }
        });
      },
      error: (err) => {
        console.error('Nije moguće dobiti distancu, šaljem 0:', err);
        this.rideService.finishRide(this.activeRide.id, 0).subscribe(() => this.completeRideFlow());
      }
    });
  } catch (err) {
    console.error('Greška u koordinatama:', err);
    // Ako sve propadne, pošalji 0, backend će rešiti fallback
    this.rideService.finishRide(this.activeRide.id, 0).subscribe(() => this.completeRideFlow());
  }
}

  private completeRideFlow() {
    this.auth.setInDrive(false);
    this.showFinishNotification.set(true);
    alert("Ride finished! Summary sent via email.");
    this.router.navigate(['/map']);
  }

  // ---------------- Reporting route issues ----------------
  onReport() {
    const reason = prompt("Why is the route inconsistent? (Max 500 chars)");
    if (!reason) return;

    const trimmed = reason.trim();
    if (trimmed.length === 0) { alert("Reason cannot be empty."); return; }
    if (trimmed.length > 500) { alert("Reason too long."); return; }

    if (!this.activeRide) { alert("No active ride."); return; }

    this.rideService.reportInconsistency(this.activeRide.id, trimmed).subscribe({
      next: () => alert("Inconsistency reported successfully."),
      error: err => alert("Failed to report inconsistency: " + (err.error?.message || "Unknown"))
    });
  }
}