import { Component, inject, signal, ViewChild, AfterViewInit } from '@angular/core';
import { MapViewComponent } from '../../components/map-view/map-view';
import { RouterOutlet, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service'; 
import { Map } from '../../services/map'; // Import your map service

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
    const coordinates: [number, number][] = [
      [19.8335, 45.2671],
      [19.3956, 45.2497],
      [19.6667, 46.1000] 
    ];
  
    setTimeout(() => {
      if (this.mapView && this.mapView.map) {
        this.mapView.drawRouteWithStops(coordinates);
      }
    }, 400);
    // // Check if we have addresses stored in the service
    // if (this.auth.origin() && this.auth.destination()) {
    //   await this.drawRouteOnLoad();
    // }
  }

  // Private helper to geocode and draw the line
  private async drawRouteOnLoad() {
    try {
      // 1. Convert address strings to coordinates using the Map service
      const originCoords = await this.mapService.geocodeAddress(this.auth.origin());
      const destCoords = await this.mapService.geocodeAddress(this.auth.destination());

      // 2. We must wait a tiny bit to ensure Mapbox has fully loaded its internal layers
      // Mapbox can be picky if you try to add a layer the exact millisecond it's created
      setTimeout(() => {
        if (this.mapView && this.mapView.map) {
          this.mapView.drawRouteAndCalculateETA(originCoords, destCoords);
        }
      }, 500); 

    } catch (err) {
      console.error('Failed to draw route on load:', err);
    }
  }

  // Update ETA when the child map finishes calculation
  onEtaReceived(minutes: number) {
    this.auth.eta.set(minutes);
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
  onReport() {
    const reason = prompt("Why is the route inconsistent?");
    if (reason) {
      console.log("Reported reason:", reason);
      alert("Inconsistency reported.");
    }
  }

  
}