import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DriverRideHistory } from '../../services/models/driver-history';
import { DriverService } from '../../services/driver.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-driver-history',
  standalone: true,
  imports: [CommonModule, FormsModule], 
  templateUrl: './driver-history.html',
  styleUrls: ['./driver-history.css'],
})
export class DriverHistory implements OnInit {
  allRides: DriverRideHistory[] = []; // Original list from server
  rides: DriverRideHistory[] = [];    // List that is displayed (filtered)

  // Variables for filter
  fromDate: string = '';
  toDate: string = '';

  constructor(private driverService: DriverService, 
              private authService: AuthService,
              private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    const driverId = this.authService.getIdFromToken();
    let driverIdNum = 3; 
    if (driverId !== null) driverIdNum = Number(driverId);

    this.driverService.getDriverHistory(driverIdNum).subscribe({
      next: (data: DriverRideHistory[]) => {
        this.allRides = data;
        this.rides = [...data]; // Initially, all drives are shown
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  // Function for searching/filtering
  searchByDate() {
    if (!this.fromDate && !this.toDate) {
      this.rides = [...this.allRides];
      return;
    }

    this.rides = this.allRides.filter(ride => {
      const rideDate = new Date(ride.startTime).getTime();
      
      // Set boundaries (if field is not filled, go to infinity)
      const start = this.fromDate ? new Date(this.fromDate).getTime() : -Infinity;
      const end = this.toDate ? new Date(this.toDate).setHours(23, 59, 59, 999) : Infinity;

      return rideDate >= start && rideDate <= end;
    });
  }

  // Function for resetting filters
  clearFilter() {
    this.fromDate = '';
    this.toDate = '';
    this.rides = [...this.allRides]; // Return original list
  }
}