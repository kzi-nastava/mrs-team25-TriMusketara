import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-driver-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-profile.html',
  styleUrls: ['../../shared/profile-sidebar.css', './driver-profile.css'],
})
export class DriverProfile {
  
  @Output() viewSelected = new EventEmitter<string>();

  driverButtons = [
    //At the moment, everything exept ride history shows the map view
    {label: 'Scheduled rides', view: 'map'}, // should be scheduled for example
    {label: 'Ride history', view: 'driver-history'}, 
    {label: 'Change information', view: 'map'}, // should be change-info for example
    {label: 'Reports', view: 'map'}, // should be reports for example
    {label: 'Notes', view: 'map'}, // should be notes for example
    {label: 'Support', view: 'map'}, // should be support for example
    {label: 'Log out', redText: true, view: 'map'} // should be logout for example
  ]

  // Emit the selected view when a button is clicked
  onButtonClick(view: string | undefined) {
    if (view) {
      this.viewSelected.emit(view);
    }
  }
}