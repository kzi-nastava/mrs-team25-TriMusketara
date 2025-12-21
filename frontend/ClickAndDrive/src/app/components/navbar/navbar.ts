import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-navbar',
  standalone: true,
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class NavbarComponent {
  // Can be changed manually to test: 'guest', 'user', 'admin'
  userType: 'guest' | 'user' | 'admin' = 'user'; 
  userName = signal('TriMusketara');

  onProfileClick() {
    console.log('Opening user profile:', this.userName());
    // Here you can add navigation, for example: this.router.navigate(['/profile']);
    alert('Profile button clicked!');
  }
}