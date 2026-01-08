import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { DriveInProgress } from '../drive-in-progress/drive-in-progress';
import { RidePopup } from '../../shared/ride-popup';

@Component({
  selector: 'app-scheduled-rides',
  imports: [DriveInProgress],
  templateUrl: './scheduled-rides.html',
  styleUrl: './scheduled-rides.css',
})
export class ScheduledRides {
  constructor(
    private router: Router,
    public ridePopup: RidePopup
  ) {}

  onStartClick() {
    this.router.navigate(['drive-in-progress']);
  }
  
  openPopup() {
    this.ridePopup.open();
  }
}
