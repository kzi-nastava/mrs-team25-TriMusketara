import { ChangeDetectorRef, Component, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RideOrderingService } from '../../services/ride.service';
import { AdminRideStateDTO } from '../../services/models/admin-ride-state';
import { MapViewComponent } from '../../components/map-view/map-view'; 
import { Map } from '../../services/map'; 

@Component({
  selector: 'app-admin-active-rides',
  standalone: true, 
  imports: [CommonModule, MapViewComponent], 
  templateUrl: './admin-active-rides.html',
  styleUrls: ['./admin-active-rides.css']
})
export class AdminActiveRides implements OnInit {
  activeRides: AdminRideStateDTO[] = [];
  
  // Kontrola prikaza
  isMonitoring = false;
  selectedRide: AdminRideStateDTO | null = null;
  cdr = inject(ChangeDetectorRef);

  @ViewChild(MapViewComponent) mapView!: MapViewComponent;

  private mapService = inject(Map);
  private rideService = inject(RideOrderingService);

  ngOnInit() {
    this.loadActiveRides();
  }

  loadActiveRides() {
    this.rideService.getActiveRides().subscribe({
      next: (res) => {
        this.activeRides = res;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Greška pri učitavanju vožnji:', err)
    });
  }

  async followRide(ride: AdminRideStateDTO) {
    this.selectedRide = ride;
    this.isMonitoring = true;

    
    setTimeout(async () => {
      if (this.mapView && ride.originAddress && ride.destinationAddress) {
        try {
          
          const originCoords = await this.mapService.geocodeAddress(ride.originAddress);
          const destCoords = await this.mapService.geocodeAddress(ride.destinationAddress);

          if (originCoords && destCoords) {
            //simulate tracking
            this.mapView.drawRouteWithStops([originCoords, destCoords], true);
          }
        } catch (err) {
          console.error('Greška u geokodiranju:', err);
        }
      }
    }, 200);
  }

  backToTable() {
    this.isMonitoring = false;
    this.selectedRide = null;
    this.loadActiveRides();
  }
}