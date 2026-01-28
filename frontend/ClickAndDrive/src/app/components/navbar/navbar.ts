import { Component, signal } from '@angular/core';
import { Router, RouterOutlet, ActivatedRoute } from '@angular/router';
import { PassengerProfile } from '../../layout/passenger-profile/passenger-profile'
import { DriverProfile } from '../../layout/driver-profile/driver-profile';
import { AdminProfile } from '../../layout/admin-profile/admin-profile';
import { RidePopup } from '../../shared/ride-popup';
import { ProfileSidebarService } from '../../services/profile-sidebar.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
  imports: [PassengerProfile, DriverProfile, AdminProfile, RouterOutlet] // <-- RouterModule dodan
})
export class NavbarComponent {

  constructor(
    private router: Router,
    private ridePopup: RidePopup,
    public profileSidebar: ProfileSidebarService,
    private route: ActivatedRoute,
    public auth: AuthService // public so you have it in HTML
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

  // Order route navigation
  orderRideClick() {
    this.ridePopup.open();
  }

  goToHome() {
    this.auth.setInDrive(false);
    this.auth.setRideData('', '');
    this.router.navigate(['/map']);
  }

  // Logic for PANIC button 
  onPanic() {
    console.log("Panic triggered!");
    alert("Emergency notification sent to administrator!");
  }
  onTestRideClick() {
    this.auth.setRideData('Bulevar Oslobođenja 45', 'Cara Dušana 12');
    this.auth.setInDrive(true);
    this.router.navigate(['drive-in-progress']);
  }
}
