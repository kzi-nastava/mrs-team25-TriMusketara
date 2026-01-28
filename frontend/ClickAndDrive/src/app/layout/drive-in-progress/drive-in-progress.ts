import { Component, inject, signal, ViewChild, AfterViewInit } from '@angular/core';
import { MapViewComponent } from '../../components/map-view/map-view';
import { RouterOutlet, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service'; 
import { Map } from '../../services/map'; // Import your map service
import { RideOrderingService } from '../../services/ride.service';

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
  private mapService = inject(Map); // Inject Map service

  // Access the child map component
  @ViewChild(MapViewComponent) mapView!: MapViewComponent;

  showFinishNotification = signal(false);

  // ngAfterViewInit runs after the HTML (and the map) is ready
  async ngAfterViewInit() {
    this.auth.setRideData('Bulevar Oslobođenja 45', 'Cara Dušana 12');
    const coordinates: [number, number][] = [
      [19.8335, 45.2671],
      [19.8253, 45.2471],
      [19.6667, 46.1000] 
    ];
  
    // setTimeout(() => {
    //   if (this.mapView && this.mapView.map) {
    //     this.mapView.drawRouteWithStops(coordinates);
    //   }
    // }, 400);
    // // Check if we have addresses stored in the service
    if (this.auth.origin() && this.auth.destination()) {
      await this.drawRouteOnLoad();
    }
  }

  // Private helper to geocode and draw the line
  private async drawRouteOnLoad() {
    try {
      // 1. Convert address strings to coordinates using the Map service
      console.log('Drawing route for:', this.auth.origin(), this.auth.destination());
      const originCoords = await this.mapService.geocodeAddress(this.auth.origin());
      const destCoords = await this.mapService.geocodeAddress(this.auth.destination());

      // 2. We must wait a tiny bit to ensure Mapbox has fully loaded its internal layers
      // Mapbox can be picky if you try to add a layer the exact millisecond it's created
      setTimeout(() => {
        if (this.mapView && this.mapView.map) {
          this.mapView.drawRouteAndCalculateETA(originCoords, destCoords);
        }
      }, 200); 

    } catch (err) {
      console.error('Failed to draw route on load:', err);
    }
  }

  // Update ETA when the child map finishes calculation
  onEtaReceived(minutes: number) {
    this.auth.eta.set(minutes);
  }

  onRouteCalculated(info: { durationMinutes: number; distanceKm: number }) {
    this.auth.eta.set(info.durationMinutes);
    console.log('Route info:', info);
  }


  // Method to handle when the passenger simulates/detects ride end 
  onFinishPassenger() {
    // 1. Update global state to stop the drive tracking
    this.auth.setInDrive(false);
    
    // 2. Show the post-ride notification overlay
    this.showFinishNotification.set(true);
  }

  // Method called from the notification to start rating the ride
  openRating() {
    this.showFinishNotification.set(false);
    console.log("Opening rating screen...");
    this.router.navigate(['/rate-ride']);
  }

  // Method to return to home/order screen 
  resetToOrder() {
    this.showFinishNotification.set(false);
    this.router.navigate(['/map']); 
  }

  // Logic for reporting route inconsistency
  private rideService = inject(RideOrderingService); // Inject service

onReport() {
  const reason = prompt("Why is the route inconsistent? (Max 500 chars)");

  // --- FRONTEND VALIDATION ---
  if (reason === null) return; // User clicked Cancel

  const trimmedReason = reason.trim();

  if (trimmedReason.length === 0) {
    alert("Reason cannot be empty.");
    return;
  }

  if (trimmedReason.length > 500) {
    alert("Reason is too long. Maximum 500 characters allowed.");
    return;
  }

  // --- SEND TO BACKEND ---
  //const rideId = this.auth.currentRideId(); 
  // const rideId = 37; 
  // this.rideService.reportInconsistency(rideId, trimmedReason).subscribe({
  //   next: (res) => {
  //     console.log("Report saved:", res);
  //     alert("Inconsistency successfully reported.");
  //   },
  //   error: (err) => {
  //     console.error("Report failed:", err);
  //     alert("Failed to report inconsistency: " + (err.error?.message || "Unknown error"));
  //   }
  // });

  
}

  onFinishDriver() {
    // Hardcoded rideId for demo purposes
    const testRideId = 1; 

    console.log("Finishing ride for demo...");

    this.rideService.finishRide(testRideId).subscribe({
      next: (res) => {
        console.log("Ride finished successfully:", res);
        this.completeRideFlow();
      },
      error: (err) => {
        // Even if it returns an error (because rideId 1 might not exist), 
        // your backend CATCH block will send that test email!
        console.log("Backend error (expected for demo), but email should be sent.");
        this.completeRideFlow();
      }
    });
  }

  // Helper method to clear the screen
  private completeRideFlow() {
    this.auth.setInDrive(false);
    this.showFinishNotification.set(true);
    alert("Check your email! Ride summary sent.");
    this.router.navigate(['/map']);
  }

  onStopDriver() {
    alert("Drive stopped.")
    this.auth.setInDrive(false);
    this.router.navigate(['/map']);
    
  }
}