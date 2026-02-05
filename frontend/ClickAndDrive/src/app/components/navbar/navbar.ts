import { Component, signal, inject } from '@angular/core';
import { Router, RouterOutlet, ActivatedRoute } from '@angular/router';
import { PassengerProfile } from '../../layout/passenger-profile/passenger-profile'
import { DriverProfile } from '../../layout/driver-profile/driver-profile';
import { AdminProfile } from '../../layout/admin-profile/admin-profile';
import { RidePopup } from '../../shared/ride-popup';
import { ProfileSidebarService } from '../../services/profile-sidebar.service';
import { AuthService } from '../../services/auth.service';
import { PanicRequest, PanicService } from '../../services/panic.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
  imports: [PassengerProfile, DriverProfile, AdminProfile, RouterOutlet]
})
export class NavbarComponent {

  panicPressed = signal(false); // Da li je panic već pritisnut
  private panicService = inject(PanicService);

  constructor(
    private router: Router,
    private ridePopup: RidePopup,
    public profileSidebar: ProfileSidebarService,
    private route: ActivatedRoute,
    public auth: AuthService
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
  if (this.panicPressed()) {
    alert('PANIC already triggered for this ride!');
    return;
  }

  const confirm = window.confirm(
    '🚨 Are you sure you want to trigger PANIC?\n\nThis will immediately notify administrators of a serious problem with your ride.'
  );

  if (!confirm) return;

  const rideDataStr = localStorage.getItem('activeRideData');
  if (!rideDataStr) {
    alert('No active ride found!');
    return;
  }

  const activeRide = JSON.parse(rideDataStr);
  const userId = this.auth.getUserId();

  if (!userId) {
    alert('User not found');
    return;
  }

  const panicRequest: PanicRequest = {
    rideId: activeRide.id,
    guest: activeRide.guest === true,
    userId: userId
  };

  this.panicService.triggerPanic(panicRequest).subscribe({
    next: (response) => {
      this.panicPressed.set(true);
      alert('🚨 PANIC TRIGGERED!\n\nAdministrators have been notified and will take immediate action.');
      console.log('Panic response:', response);
    },
    error: (err) => {
      console.error('Failed to trigger panic:', err);
      alert('Failed to trigger PANIC: ' + (err.error?.message || 'Unknown error'));
    }
  });
}

  onTestRideClick() {
    this.auth.setRideData('Bulevar Oslobođenja 45', 'Cara Dušana 12');
    this.auth.setInDrive(true);
    this.router.navigate(['drive-in-progress']);
  }

  // Navigate to admin panic notifications
  goToPanicNotifications() {
    this.router.navigate(['/panic-notifications']);
  }
}