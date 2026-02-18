import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // OBAVEZNO ZA ngFor
import { RideOrderingService} from '../../services/ride.service';
import { Router } from '@angular/router';
import { AdminRideStateDTO } from '../../services/models/admin-ride-state';

@Component({
  selector: 'app-admin-active-rides',
  standalone: true, 
  imports: [CommonModule], 
  templateUrl: './admin-active-rides.html',
  styleUrls: ['./admin-active-rides.css']
})
export class AdminActiveRides implements OnInit {
  activeRides: AdminRideStateDTO[] = [];

  constructor(
    private rideService: RideOrderingService, 
    private router: Router
  ) {}

  ngOnInit() {
    this.rideService.getActiveRides().subscribe({
      next: (res) => {
        this.activeRides = res;
      },
      error: (err) => {
        console.error('403 ili druga greška:', err);
        // Ako je 403, proveri da li je ruta /api/admin/** dozvoljena za ADMIN rolu u Springu
      }
    });
  }

  followRide(ride: AdminRideStateDTO) {
    this.router.navigate(['/main-page'], { queryParams: { monitorRideId: ride.rideId } });
  }
}
