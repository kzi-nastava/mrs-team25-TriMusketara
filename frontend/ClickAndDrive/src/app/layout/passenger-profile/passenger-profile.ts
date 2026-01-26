import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-passenger-profile',
  templateUrl: './passenger-profile.html',
  styleUrls: ['../../shared/profile-sidebar.css', './passenger-profile.css'],
})
export class PassengerProfile {

  passengerButtons = [
    { label: 'Favorite routes', route: 'favorite-routes' },
    { label: 'Ride history' },
    { label: 'Change information', route: 'change-information-page' },
    { label: 'Reports' },
    { label: 'Notes' },
    { label: 'Support' },
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
