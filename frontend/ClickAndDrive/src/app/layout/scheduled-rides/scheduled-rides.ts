import { Component, OnInit, signal, ChangeDetectorRef  } from '@angular/core';
import { Router } from '@angular/router';
import { DriveInProgress } from '../drive-in-progress/drive-in-progress';
import { RidePopup } from '../../shared/ride-popup';
import { AuthService } from '../../services/auth.service';
import { RideOrderingService, DriverRideDTO } from '../../services/ride.service';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface ScheduledRide {
  id: number;
  origin: string;
  destination: string;
  scheduledTime: string;
  guest: boolean;
}

@Component({
  selector: 'app-scheduled-rides',
  imports: [DriveInProgress, DatePipe, FormsModule, CommonModule],
  templateUrl: './scheduled-rides.html',
  styleUrl: './scheduled-rides.css',
})
export class ScheduledRides implements OnInit {
  selectedRide?: ScheduledRide;
  rides: ScheduledRide[] = [];
  page = 1;
  size = 6;
  totalPages = 0;
  reason = '';

  cancelingRideId?: number;
  cancelReason = '';

  constructor(
    private router: Router,
    public auth: AuthService,
    public ridePopup: RidePopup,
    private rideService: RideOrderingService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadRides();
  }

  loadRides() {
    this.rideService.getScheduledRides(this.page, this.size)
      .subscribe({
        next: res => {
          this.rides = res.content;
          this.totalPages = res.totalPages;
          console.log(this.rides);
          this.cdr.detectChanges();
        },
        error: err => console.error(err)
      });
  }

  nextPage() {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.loadRides();
    }
  }

  prevPage() {
    if (this.page > 0) {
      this.page--;
      this.loadRides();
    }
  }

  cancelRide(ride: ScheduledRide) {
  if (!this.cancelReason || this.cancelReason.trim() === '') {
    alert('Please enter a reason for cancellation');
    return;
  }

  const userId = this.auth.getUserId();

  this.rideService.cancelRide(ride.id, userId, this.cancelReason)
    .subscribe({
      next: () => {
        this.rides = this.rides.filter(r => r.id !== ride.id);
        this.ridePopup.close();
        this.cancelReason = '';
        this.selectedRide = undefined;
      },
      error: err => {
        console.error(err);
        alert('Error cancelling ride');
      }
    });
  }

  onStartClick() {
    
    const coords: [number, number][] = [
      [19.8350, 45.2517], 
      [19.8253, 45.2471]
    ];

    //for now hardcoded, later from scheduled ride data
    this.auth.setRideData('Modene 3', 'Grčkoškolska 4');
    this.auth.setInDrive(true);

    
    this.router.navigate(['drive-in-progress']);
  }
  
  openPopup(rideId: number) {
    this.cancelingRideId = rideId;
    this.cancelReason = '';
    this.ridePopup.open();
  }
}
