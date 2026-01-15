import { Component, signal } from '@angular/core';
import { Router, RouterOutlet, ActivatedRoute } from '@angular/router';
import { PassengerProfile } from '../../layout/passenger-profile/passenger-profile'
import { DriverProfile } from '../../layout/driver-profile/driver-profile';
import { AdminProfile } from '../../layout/admin-profile/admin-profile';
import { RidePopup } from '../../shared/ride-popup';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
  imports: [PassengerProfile, DriverProfile, AdminProfile, RouterOutlet] // <-- RouterModule dodan
})
export class NavbarComponent {
  // Flag to show profile sidebar
  showSidebar = false;

  constructor(
    private router: Router,
    private ridePopup: RidePopup,
    private route: ActivatedRoute,
    public auth: AuthService // public so you have it in HTML
  ) {}

  onProfileClick() {
    this.showSidebar = !this.showSidebar;
    console.log('Opening user profile:', this.auth.userName());
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

  openPopup() {
    this.ridePopup.open();
  }

  // Order route navigation
  orderRideClick() {
    this.ridePopup.open();
  }
}
