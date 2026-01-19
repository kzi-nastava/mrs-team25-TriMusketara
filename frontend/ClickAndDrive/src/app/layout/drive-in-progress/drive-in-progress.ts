import { Component, inject, signal } from '@angular/core'; // Added signal to imports
import { MapViewComponent } from '../../components/map-view/map-view';
import { RouterOutlet, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service'; 

@Component({
  selector: 'app-drive-in-progress',
  standalone: true, 
  imports: [RouterOutlet, MapViewComponent],
  templateUrl: './drive-in-progress.html',
  styleUrl: './drive-in-progress.css',
})
export class DriveInProgress {
  auth = inject(AuthService);
  router = inject(Router);

  // Signal for showing/hiding the post-ride notification
  showFinishNotification = signal(false);

  // Method to handle when the passenger simulates/detects ride end 
  onFinishPassenger() {
    // 1. Update global state to stop the drive tracking
    this.auth.setInDrive(false);
    
    // 2. Show the post-ride notification overlay
    this.showFinishNotification.set(true);
  }

  // Method called from the notification to start rating the ride
  openRating() {
    this.showFinishNotification.set(false);
    console.log("Opening rating screen...");
    this.router.navigate(['/rate-ride']);
  }

  // Method to return to home/order screen 
  resetToOrder() {
    this.showFinishNotification.set(false);
    this.router.navigate(['/map']); 
  }

  // Logic for reporting route inconsistency
  onReport() {
    const reason = prompt("Why is the route inconsistent?");
    if (reason) {
      console.log("Reported reason:", reason);
      alert("Inconsistency reported.");
    }
  }

  
}