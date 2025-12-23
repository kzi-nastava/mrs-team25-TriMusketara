import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-driver-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-profile.html',
  styleUrls: ['../../shared/profile-sidebar.css', './driver-profile.css'],
})
export class DriverProfile {
  
  constructor(private router: Router) {}

  driverButtons = [
    {label: 'Scheduled rides', route: 'scheduled-rides'},
    {label: 'Ride history', route: 'driver-history'},
    {label: 'Change information', route: 'change-info'},
    {label: 'Reports', route: 'reports'},
    {label: 'Notes', route: 'notes'},
    {label: 'Support', route: 'support'},
    {label: 'Log out', redText: true, route: 'logout'}
  ]

  // Navigate to the selected route when a button is clicked
  onButtonClick(route: string | undefined) {
    if (route) {
      this.router.navigate([route]);
    }
  }
}