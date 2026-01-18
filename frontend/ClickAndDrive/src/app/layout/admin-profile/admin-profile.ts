import { Component } from '@angular/core';
import { Router } from '@angular/router';


@Component({
  selector: 'app-admin-profile',
  imports: [],
  templateUrl: './admin-profile.html',
  styleUrls: ['./admin-profile.css'],
})
export class AdminProfile {
  adminButtons = [
    {label: "Driver registration", route: 'driver-registration'},
    {label: 'Check current rides'},
    {label: 'Change prices'},
    {label: 'Ride history'},
    {label: 'Requests'},
    {label: 'Change information', route: 'change-information-page'},
    {label: 'Support'},
    {label: 'Reports'},
    {label: 'Notes'},
    {label: 'Log out', redText: true}
  ];

  constructor(private router: Router) {}

  get leftButtons() {
    return this.adminButtons.slice(0, 5);
  }

  get rightButtons() {
    return this.adminButtons.slice(5);
  }

  onButtonClick(route: string | undefined) {
    if (route) {
      this.router.navigate([route]);
    }
  }
}
