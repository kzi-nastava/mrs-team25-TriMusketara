import { Component } from '@angular/core';

@Component({
  selector: 'app-driver-profile',
  imports: [],
  templateUrl: './driver-profile.html',
  styleUrls: ['../../shared/profile-sidebar.css', './driver-profile.css'],
})
export class DriverProfile {
  driverButtons = [
    {label: 'Scheduled rides'},
    {label: 'Ride history'},
    {label: 'Change information'},
    {label: 'Reports'},
    {label: 'Notes'},
    {label: 'Support'},
    {label: 'Log out', redText: true}
  ]
}
