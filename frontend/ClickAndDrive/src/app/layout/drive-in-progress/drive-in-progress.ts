import { Component, inject } from '@angular/core';
import { MapViewComponent } from '../../components/map-view/map-view';
import { RouterOutlet, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service'; // Putanja do tvog servisa

@Component({
  selector: 'app-drive-in-progress',
  standalone: true, // Ako koristite standalone
  imports: [RouterOutlet, MapViewComponent],
  templateUrl: './drive-in-progress.html',
  styleUrl: './drive-in-progress.css',
})
export class DriveInProgress {
  auth = inject(AuthService);
  router = inject(Router);

  // Your methods (Student 2)
  onPanic() {
    console.log("Panic triggered!");
    // Logic for PANIC (2.6.3)
  }

  onReportInconsistency() {
    const reason = prompt("Enter inconsistency reason:");
    console.log("Reported:", reason);
    
  }

  // Funkcija za Report dugme (MORA SE ZVATI onReport jer je tako u HTML-u)
  onReport() {
    const reason = prompt("Why is the route inconsistent?");
    if (reason) {
      console.log("Reported reason:", reason);
      alert("Inconsistency reported.");
    }
  }

  // Funkcija za Rate dugme (MORA SE ZVATI onRate jer je tako u HTML-u)
  onRate() {
    console.log("Navigating to rating screen...");
    //this.router.navigate(['/rating']); // Otkomentariši kada napraviš rutu za ocenjivanje
    alert("Redirecting to rating page...");
  }

  
  goToRating() {
    this.router.navigate(['/rate-ride']);
  }
}