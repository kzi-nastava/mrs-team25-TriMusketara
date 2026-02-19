import { Component, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PassengerRideHistory } from '../../services/models/passenger-ride-history';
import { PassengerService } from '../../services/passenger.service';
import { AuthService } from '../../services/auth.service';
import { MapViewComponent } from '../../components/map-view/map-view';
import { RideOrderingService } from '../../services/ride.service';
import { RideOrderResponse } from '../../services/models/ride-order-response';
import { RideOrderCreate } from '../../services/models/ride-order-create';
import { Router, RouterOutlet, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-passenger-history',
  standalone: true,
  imports: [CommonModule, FormsModule, MapViewComponent, RouterOutlet],
  templateUrl: './passenger-history.html',
  styleUrl: './passenger-history.css',
})
export class PassengerHistory implements OnInit {
  @ViewChild(MapViewComponent) mapComponent!: MapViewComponent;


  allRides: PassengerRideHistory[] = [];
  rides: PassengerRideHistory[] = [];

  selectedRide: any = null;
  showDetails: boolean = false;

  stars = [1, 2, 3, 4, 5];

  fromDate: string = '';
  toDate: string = '';
  sortField: string = 'startTime';

  constructor(
    private router: Router,
    private passengerService: PassengerService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private rideService: RideOrderingService
  ) {}

  ngOnInit() {
    const passengerId = this.authService.getUserId();
    console.log(passengerId);

    if (!passengerId) return;

    this.passengerService.getPassengerHistory(Number(passengerId)).subscribe({
      next: (data) => {
        this.allRides = data;
        this.rides = [...data];
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  searchByDate() {

    if (!this.fromDate && !this.toDate) {
      this.rides = [...this.allRides];
      return;
    }

    const now = new Date().getTime();
    const start = this.fromDate ? new Date(this.fromDate).getTime() : -Infinity;

    if (this.fromDate && start > now) {
      alert("Date 'From' cannot be in the future!");
      return;
    }

    if (this.fromDate && this.toDate && start > new Date(this.toDate).getTime()) {
      alert("Date 'From' must be before Date 'To'!");
      return;
    }

    this.rides = this.allRides.filter(ride => {

      const rideDate = new Date(ride.startTime).getTime();

      const adjustedEnd = this.toDate
        ? new Date(this.toDate).setHours(23, 59, 59, 999)
        : Infinity;

      return rideDate >= start && rideDate <= adjustedEnd;
    });
  }

  clearFilter() {
    this.fromDate = '';
    this.toDate = '';
    this.rides = [...this.allRides];
  }

  openDetails(rideId: number) {
    this.passengerService.getRideDetails(rideId).subscribe({
      next: (data) => {
        this.selectedRide = data;
        this.showDetails = true;
        console.log(data)

        setTimeout(() => {
          if (this.mapComponent) {
            this.mapComponent.drawRouteAndCalculateETA(
              [
                this.selectedRide.origin.longitude,
                this.selectedRide.origin.latitude
              ],
              [
                this.selectedRide.destination.longitude,
                this.selectedRide.destination.latitude
              ]
            );
          }
        }, 200);
      }
    });
  }


  closeDetails() {
    this.showDetails = false;
  }

  getDriverRating(): number {
    return this.selectedRide?.review?.driverRating ?? 0;
  }

  getVehicleRating(): number {
    return this.selectedRide?.review?.vehicleRating ?? 0;
  }

  getStarsArray(): number[] {
    return [1, 2, 3, 4, 5];
  }

  sortBy() {
      this.rides.sort((a: any, b: any) => {
          switch(this.sortField) {
              case 'startTime': return new Date(b.startTime).getTime() - new Date(a.startTime).getTime();
              case 'endTime': return new Date(b.endTime).getTime() - new Date(a.endTime).getTime();
              case 'totalPrice': return b.totalPrice - a.totalPrice;
              case 'status': return b.status.localeCompare(a.status);
              default: return 0;
          }
      });
  }

  reorderNow(ride: RideOrderResponse) {

  // Scheduled time bar 2 minuta u budućnosti
  const nowPlusTwoMinutes = new Date(Date.now() + 62 * 60_000);
  console.log('Scheduled time:', nowPlusTwoMinutes.toISOString());

  // Sigurno mapiranje passengerEmails
  const passengerEmails: string[] = this.selectedRide.passengers
    ?.map((p: any) => p.email)
    .filter((e: any) => !!e) || [];


  console.log(this.authService.getUserIdFromToken())
  // Request
  const request: RideOrderCreate = {
    passengerId: this.authService.getUserIdFromToken() || 0,
    origin: this.selectedRide.origin,
    destination: this.selectedRide.destination,
    stops: this.selectedRide.stops || [],
    passengerEmails,
    vehicleType: this.selectedRide.vehicleType || 'STANDARD', // fallback
    scheduledTime: nowPlusTwoMinutes.toISOString(),
    babyFriendly: this.selectedRide.babyFriendly || false,
    petFriendly: this.selectedRide.petFriendly || false,
    durationMinutes: this.selectedRide.durationMinutes || 10, // fallback
    distanceKm: this.selectedRide.distanceKm || 1 // fallback
  };

  console.log(request);

  // Poziv servisa
  this.rideService.createRide(request).subscribe({
    next: (res) => {
      alert("Ride successfully reordered!");
      this.closeDetails();
      this.router.navigate(['map']); // premesti ovde da bude posle uspeha
    },
    error: (err) => {
      console.error('Reorder error:', err);
      alert("Failed to reorder ride: " + (err?.error?.message || err.message));
    }
  });
}

}
