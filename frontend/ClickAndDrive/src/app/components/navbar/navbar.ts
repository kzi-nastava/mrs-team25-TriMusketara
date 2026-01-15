import { Component, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { PassengerProfile } from '../../layout/passenger-profile/passenger-profile'
import { DriverProfile } from '../../layout/driver-profile/driver-profile';
import { AdminProfile } from '../../layout/admin-profile/admin-profile';
import { RidePopup } from '../../shared/ride-popup';
import { ProfileSidebarService } from '../../services/profile-sidebar.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
  imports: [PassengerProfile, DriverProfile, AdminProfile, RouterOutlet] // <-- RouterModule dodan
})
export class NavbarComponent {
  // Can be changed manually to test: 'guest', 'user', 'admin'
  userType: 'guest' | 'user' | 'driver' |'admin' = 'admin';

  inDrive: boolean = false;

  userName = signal('TriMusketara');

  constructor(
    private router: Router,
    private ridePopup: RidePopup,
    public profileSidebar: ProfileSidebarService
  ) {}

  onProfileClick() {
    this.profileSidebar.toggle();
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
    this.profileSidebar.close();
  }

  openPopup() {
    this.ridePopup.open();
  }
}
