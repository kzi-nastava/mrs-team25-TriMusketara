import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-passenger-profile',
  imports: [],
  templateUrl: './passenger-profile.html',
  styleUrls: ['../../shared/profile-sidebar.css', './passenger-profile.css'],
})
export class PassengerProfile {
  passengerButtons = [
    {label: 'Favorite routes', route: 'favorite-routes'},
    {label: 'Ride history'},
    {label: 'Change information', route: 'change-information-page'},
    {label: 'Reports'},
    {label: 'Notes'},
    {label: 'Support'},
    {label: 'Log out', redText: true}
  ];
  
  constructor(private router: Router) {}

  onButtonClick(route: string | undefined) {
    if (route) {
      this.router.navigate([route]);
    }
  }

}