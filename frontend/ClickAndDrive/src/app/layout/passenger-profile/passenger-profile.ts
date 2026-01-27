import { ChangeDetectorRef, Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { UserProfileInformation } from '../../services/models/user-profile-information';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-passenger-profile',
  imports: [CommonModule],
  templateUrl: './passenger-profile.html',
  styleUrls: ['../../shared/profile-sidebar.css', './passenger-profile.css'],
})
export class PassengerProfile {

  passengerButtons = [
    { label: 'Favorite routes', route: 'favorite-routes' },
    { label: 'Ride history' },
    { label: 'Change information', route: 'change-information-page' },
    { label: 'Reports' },
    { label: 'Notes' },
    { label: 'Support' },
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

  onButtonClick(button: any) {
    if (button.logout) {
      this.authService.logout();
      return;
    }

    if (button.route) {
      this.router.navigate([button.route]);
    }
  }
}
