import { Component, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { UserProfileInformation } from '../../services/models/user-profile-information';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-profile',
  imports: [CommonModule],
  templateUrl: './admin-profile.html',
  styleUrls: ['./admin-profile.css'],
})
export class AdminProfile {

  adminButtons = [
    { label: 'Driver registration', route: 'driver-registration' },
    { label: 'Check current rides' },
    { label: 'Change prices' },
    { label: 'Ride history' },
    { label: 'Requests' },
    { label: 'Change information', route: 'change-information-page' },
    { label: 'Support' },
    { label: 'Reports' },
    { label: 'Notes' },
    { label: 'Log out', redText: true, logout: true }
  ];

  userName: string | null = null;
  user: UserProfileInformation | null = null;
  userId!: number;

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) {}

  
  ngOnInit(): void {
    const idFromToken = this.authService.getUserIdFromToken();

    if (!idFromToken) {
      this.authService.logout();
      this.router.navigate(['/login']);
      return;
    }

    this.userId = idFromToken;

    this.userService.getUserProfileInfo(this.userId).subscribe({
      next: (res) => {
        this.user = res;
        this.userName = this.user.name + " " + this.user.surname;
        this.cdr.detectChanges();
      },
      error: () => {
        this.authService.logout();
        this.router.navigate(['/login']);
      }
    });
  }

  get leftButtons() {
    return this.adminButtons.slice(0, 5);
  }

  get rightButtons() {
    return this.adminButtons.slice(5);
  }

  onButtonClick(button: any) {
    if (button.logout) {
      this.authService.logout();
      this.router.navigate(['/login']);
      return;
    }

    if (button.route) {
      this.router.navigate([button.route]);
    }
  }
}
