import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PassengerRideHistory } from '../../services/models/passenger-ride-history';
import { PassengerService } from '../../services/passenger.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-passenger-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './passenger-history.html',
  styleUrl: './passenger-history.css',
})
export class PassengerHistory implements OnInit {

  allRides: PassengerRideHistory[] = [];
  rides: PassengerRideHistory[] = [];

  fromDate: string = '';
  toDate: string = '';

  constructor(
    private passengerService: PassengerService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
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
}
