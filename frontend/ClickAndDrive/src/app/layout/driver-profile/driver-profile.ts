import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-driver-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-profile.html',
  styleUrls: ['../../shared/profile-sidebar.css', './driver-profile.css'],
})
export class DriverProfile {

  driverButtons = [
    { label: 'Scheduled rides', route: 'scheduled-rides' },
    { label: 'Ride history', route: 'driver-history' },
    { label: 'Change information', route: 'change-info' },
    { label: 'Reports', route: 'reports' },
    { label: 'Notes', route: 'notes' },
    { label: 'Support', route: 'support' },
    { label: 'Log out', redText: true, logout: true }
  ];

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  onButtonClick(button: any) {
    if (button.logout) {
      this.authService.logout();
      this.router.navigate(['/login']);
      return;
    }

    if (button.route) {
      this.router.navigate([button.route]);
    }
  }
}
