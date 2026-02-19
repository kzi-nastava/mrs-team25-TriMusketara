import { Component, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin.service';
import { AdminRideHistory } from '../../services/models/admin-ride-history';
import { PassengerService } from '../../services/passenger.service';
import { MapViewComponent } from '../../components/map-view/map-view';

@Component({
  selector: 'app-admin-history',
  standalone: true,
  imports: [CommonModule, FormsModule, MapViewComponent],
  templateUrl: './admin-history.html',
  styleUrl: './admin-history.css',
})
export class AdminHistory implements OnInit {
  @ViewChild(MapViewComponent) mapComponent!: MapViewComponent;

  allRides: AdminRideHistory[] = [];
  rides: AdminRideHistory[] = [];

  fromDate: string = '';
  toDate: string = '';

  users: any[] = [];
  selectedUserId: number | null = null;
  selectedRole: string = '';

  sortField: string = 'startTime';

  selectedRide: any = null;
  showDetails: boolean = false;

  constructor(
    private adminService: AdminService,
    private cdr: ChangeDetectorRef,
    private passengerService: PassengerService
  ) {}

  ngOnInit() {
    this.adminService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
      }
    });
  }

  searchByDate() {

    const start = this.fromDate ? new Date(this.fromDate).getTime() : -Infinity;
    const end = this.toDate
      ? new Date(this.toDate).setHours(23, 59, 59, 999)
      : Infinity;

    this.rides = this.allRides.filter(ride => {
      const rideDate = new Date(ride.startTime).getTime();
      return rideDate >= start && rideDate <= end;
    });

    this.sortBy();
  }

  clearFilter() {
    this.fromDate = '';
    this.toDate = '';
    this.rides = [...this.allRides];
    this.sortBy();
  }

  loadHistory() {
    if (!this.selectedUserId || !this.selectedRole) return;

    this.adminService
      .getRideHistory(this.selectedUserId, this.selectedRole, this.fromDate, this.toDate, this.sortField)
      .subscribe(data => {
        this.allRides = data;
        this.rides = [...data];
        this.sortBy(); // lokalno sortiranje ako želiš override
      });
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


  onUserChange() {
    const user = this.users.find(u => u.id == this.selectedUserId);
    this.selectedRole = user ? user.role : '';
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
}
