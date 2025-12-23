import { Component, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { PassengerProfile } from '../../layout/passenger-profile/passenger-profile'
import { DriverProfile } from '../../layout/driver-profile/driver-profile';
import { AdminProfile } from '../../layout/admin-profile/admin-profile';

@Component({
  selector: 'app-navbar',
  standalone: true,
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
  imports: [PassengerProfile, DriverProfile, AdminProfile, RouterOutlet] // <-- RouterModule dodan
})
export class NavbarComponent {
  // Can be changed manually to test: 'guest', 'user', 'admin'
  userType: 'guest' | 'user' | 'driver' |'admin' = 'guest';

  inDrive: boolean = false;

  userName = signal('TriMusketara');

  // Flag to show profile sidebar
  showSidebar = false;

  constructor(private router: Router) {}

  onProfileClick() {
    this.showSidebar = !this.showSidebar;
    console.log('Opening user profile:', this.userName());
  }

  onLoginClick() {
    this.router.navigate(['login']);
  }

  onRegisterClick() {
    this.router.navigate(['register']);
  }

  // Navigate to a specific route and close sidebar
  handleViewSelection(route: string) {
    this.router.navigate([route]);
    this.showSidebar = false;
  }
}
