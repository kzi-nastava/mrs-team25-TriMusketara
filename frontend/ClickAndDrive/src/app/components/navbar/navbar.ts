import { Component, signal } from '@angular/core';
import {PassengerProfile} from '../../layout/passenger-profile/passenger-profile'
import { DriverProfile } from '../../layout/driver-profile/driver-profile';
import { AdminProfile } from '../../layout/admin-profile/admin-profile';

@Component({
  selector: 'app-navbar',
  standalone: true,
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
  imports: [PassengerProfile, DriverProfile, AdminProfile]
})
export class NavbarComponent {
  // Can be changed manually to test: 'guest', 'user', 'admin'
  userType: 'guest' | 'user' | 'driver' |'admin' = 'user'; 
  userName = signal('TriMusketara');

  // Flag to show profile sidebar
  showSidebar = false;

  onProfileClick() {
    console.log('Opening user profile:', this.userName());
    if (this.showSidebar) {
      this.showSidebar = false;
    } 
    else {
      this.showSidebar = true;
    }
  }
}