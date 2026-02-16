import { Component, inject, signal, ViewChild, AfterViewInit } from '@angular/core';
import { MapViewComponent } from '../../components/map-view/map-view';
import { RouterOutlet, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Map } from '../../services/map';
import { RideOrderingService } from '../../services/ride.service';
import { map } from 'rxjs';
import { WebSocketService } from '../../services/web-socket.service';
import { ToastrService } from 'ngx-toastr';

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
  private webSocketService = inject(WebSocketService);
  private toastr = inject(ToastrService);

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
    this.listenForFinish();
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

 private listenForFinish() {
  this.webSocketService.rideUpdates$.subscribe((msg) => {
    // Check the message
    if (msg.type === 'RIDE_FINISHED' && this.activeRide?.id === msg.rideId) {
      
      console.log('WebSocket: Ride finish detected', msg);

      //Passenger logic
      if (this.auth.userType() !== 'driver') {
        //Passenger only needs to see the notification
        this.completeRideFlow(msg.isGuest);
      } 
      
      // Driver logic is handled in onFinishDriver() where the HTTP call is made
      else {
        console.log('Driver received his own finish confirmation via WS.');
      }
    }
  });
}

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
      const originCoords = await this.mapService.geocodeAddress(this.activeRide.origin);
      const destCoords = await this.mapService.geocodeAddress(this.activeRide.destination);

      this.mapService.getRouteDistanceOnly(originCoords, destCoords).subscribe({
        next: (km) => {
          console.log('Finalna distanca:', km);
          
          const isGuest = this.activeRide.guest || false;

          this.rideService.finishRide(this.activeRide.id, km, isGuest).subscribe({
            next: () => this.completeRideFlow(isGuest),
            error: (err) => {
              console.error('Greška pri završetku:', err);
              this.completeRideFlow(isGuest);
            }
          });
        },
        error: (err) => {
          console.error('Greška sa distancom:', err);
          const isGuest = this.activeRide.guest || false;
          this.rideService.finishRide(this.activeRide.id, 0, isGuest).subscribe(() => this.completeRideFlow(isGuest));
        }
      });
    } catch (err) {
      const isGuest = this.activeRide.guest || false;
      this.rideService.finishRide(this.activeRide.id, 0, isGuest).subscribe(() => this.completeRideFlow(isGuest));
    }
  }

  private completeRideFlow(isGuest: boolean = false) {
    this.auth.setInDrive(false);
    this.showFinishNotification.set(true); 
    localStorage.removeItem('activeRideData');

    if (!isGuest) {
      this.toastr.success("Ride finished! Summary sent via email.", "Success");
    } else {
      this.toastr.info("Ride finished! Thank you for using Click&Drive.", "Visit us again!");
    }
  }

  // ---------------- Reporting route issues ----------------
  onReport() {
    const reason = prompt("Why is the route inconsistent? (Max 500 chars)");
    if (!reason) return;

    const trimmed = reason.trim();
    if (trimmed.length === 0) { 
      this.toastr.warning("Reason cannot be empty.", "Warning"); 
      return; 
    }
    
    if (trimmed.length > 500) { 
      this.toastr.warning("Reason is too long (max 500 chars).", "Warning"); 
      return; 
    }

    if (!this.activeRide) { 
      this.toastr.error("No active ride found.", "Error"); 
      return; 
    }

    this.rideService.reportInconsistency(this.activeRide.id, trimmed).subscribe({
      next: () => this.toastr.success("Inconsistency reported successfully.", "Report Sent"),
      error: err => this.toastr.error("Failed to report: " + (err.error?.message || "Unknown error"), "Error")
    });
  }
}