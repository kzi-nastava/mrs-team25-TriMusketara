import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { DriveInProgress } from '../drive-in-progress/drive-in-progress';
import { RidePopup } from '../../shared/ride-popup';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-scheduled-rides',
  imports: [DriveInProgress],
  templateUrl: './scheduled-rides.html',
  styleUrl: './scheduled-rides.css',
})
export class ScheduledRides {
  constructor(
    private router: Router,
    public auth: AuthService,
    public ridePopup: RidePopup
  ) {}

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
  
  openPopup() {
    this.ridePopup.open();
  }
}
