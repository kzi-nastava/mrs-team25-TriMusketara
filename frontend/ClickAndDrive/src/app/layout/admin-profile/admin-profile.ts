import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin-profile',
  templateUrl: './admin-profile.html',
  styleUrls: ['./admin-profile.css'],
})
export class AdminProfile {

  adminButtons = [
    { label: 'Driver registration', route: 'driver-registration' },
    { label: 'Check current rides' },
    { label: 'Change prices' },
    { label: 'Ride history' },
    { label: 'Requests' },
    { label: 'Change information', route: 'change-information-page' },
    { label: 'Support' },
    { label: 'Reports' },
    { label: 'Notes' },
    { label: 'Log out', redText: true, logout: true }
  ];

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  get leftButtons() {
    return this.adminButtons.slice(0, 5);
  }

  get rightButtons() {
    return this.adminButtons.slice(5);
  }

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
