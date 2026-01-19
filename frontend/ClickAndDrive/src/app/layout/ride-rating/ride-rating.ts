import { Component, EventEmitter, Output, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-ride-rating',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ride-rating.html',
  styleUrl: './ride-rating.css',
})
export class RideRating {
  // Signals for tracking selected ratings
  driverRating = signal(0);
  vehicleRating = signal(0);
  comment = signal('');
  router = inject(Router);

  // Stars array for the template loop
  stars = [1, 2, 3, 4, 5];

  // Output event to notify the parent component to close the modal
  @Output() close = new EventEmitter<void>();

  // Methods to set individual ratings
  setDriverRating(val: number) {
    this.driverRating.set(val);
  }

  setVehicleRating(val: number) {
    this.vehicleRating.set(val);
  }

  // Handle data submission
  onSubmit() {
    console.log("Rating Submitted:", {
      driver: this.driverRating(),
      vehicle: this.vehicleRating(),
      comment: this.comment()
    });
    // Add logic here to send data to the backend via a service
    this.close.emit();
    this.router.navigate(['/map']);
  }

  // Close without submitting
  onDismiss() {
    this.close.emit();
    this.router.navigate(['/map']);
  }
}