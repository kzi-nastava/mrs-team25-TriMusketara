import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar';
import { MainPageComponent } from './main-page/main-page';
import { WebSocketService } from './services/web-socket.service';
import { AuthService } from './services/auth.service';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, MainPageComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})

export class App implements OnInit {
  protected readonly title = 'ClickAndDrive';

  constructor(public router: Router, private authService: AuthService, private wsService: WebSocketService) {}

  // Here im using .includes to remove navbar from password input when registering driver, remember this!
  showNavbar(): boolean {
    return this.router.url !== '/login' && this.router.url !== '/register'
          && this.router.url !== '/driver-registration' && !this.router.url.includes('/complete-registration');
  }

  ngOnInit() {
    const userId = this.authService.getUserId();
    const role = this.authService.getRoleFromToken();
    if (userId && userId !== 0) {
      // User is logged in
      this.wsService.subscribeToUserStatus(userId);

      // Check role
      if (role === 'Passenger') {
        this.wsService.subscribeToPassengerNotes(userId);
      }

      if (role === 'Driver') {
        this.wsService.subscribeToDriverRides(userId);
      }
    }

    // If the user logs in during session
    this.authService.onLogin$.subscribe((userId: number) => {
      const role = this.authService.getRoleFromToken();
      this.wsService.subscribeToUserStatus(userId);
      
      // Check role
      if (role === 'Passenger') {
        this.wsService.subscribeToPassengerNotes(userId);
      }

      if (role === 'Driver') {
        this.wsService.subscribeToDriverRides(userId);
      }
    });
  }
}