import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-profile',
  imports: [],
  templateUrl: './admin-profile.html',
  styleUrls: ['./admin-profile.css'],
})
export class AdminProfile {
  adminButtons = [
    {label: 'Check current rides'},
    {label: 'Change prices'},
    {label: 'Ride history'},
    {label: 'Requests'},
    {label: 'Change information'},
    {label: 'Support'},
    {label: 'Reports'},
    {label: 'Notes'},
    {label: 'Log out', redText: true}
  ];

  get leftButtons() {
    return this.adminButtons.slice(0, 4);
  }

  get rightButtons() {
    return this.adminButtons.slice(4);
  }
}
