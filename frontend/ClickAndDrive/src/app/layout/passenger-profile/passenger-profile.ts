import { Component } from '@angular/core';

@Component({
  selector: 'app-passenger-profile',
  imports: [],
  templateUrl: './passenger-profile.html',
  styleUrls: ['../../shared/profile-sidebar.css', './passenger-profile.css'],
})
export class PassengerProfile {
  passengerButtons = [
    {label: 'Favorite routes'},
    {label: 'Ride history'},
    {label: 'Change information'},
    {label: 'Reports'},
    {label: 'Notes'},
    {label: 'Support'},
    {label: 'Log out', redText: true}
  ];

}