import { Component, EventEmitter, Output, signal, inject, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { RideOrderingService} from '../../services/ride.service';
import { ReviewRequest } from '../../services/models/review-request';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-ride-rating',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ride-rating.html',
  styleUrl: './ride-rating.css',
})
export class RideRating implements OnInit {
  // Inputi koje treba da proslediš komponenti kada je pozivaš
  @Input() rideId!: number; 
  // PassengerId obično izvlačiš iz AuthService-a (tokena), ovde stavljamo placeholder
  passengerId: number = 1; 

  driverRating = signal(0);
  vehicleRating = signal(0);
  comment = signal('');
  
  private router = inject(Router);
  private toastr = inject(ToastrService);
  private rideService = inject(RideOrderingService);
  private authService = inject(AuthService); // Pretpostavka da imaš auth servis

  stars = [1, 2, 3, 4, 5];

  @Output() close = new EventEmitter<void>();

  ngOnInit() {
    this.passengerId = this.authService.getUserIdFromToken() || 0;
    this.rideId = localStorage.getItem('activeRideData') ? JSON.parse(localStorage.getItem('activeRideData')!).id : 0;
  }

  setDriverRating(val: number) { this.driverRating.set(val); }
  setVehicleRating(val: number) { this.vehicleRating.set(val); }

  onSubmit() {
    console.log("Slanje rejtinga za vožnju:", this.rideId);
    console.log("ID putnika koji ocenjuje:", this.passengerId);
    console.log("Podaci:", {
      driver: this.driverRating(),
      vehicle: this.vehicleRating()
    });
    if (this.driverRating() === 0 || this.vehicleRating() === 0) {
      this.toastr.error('Please provide ratings for both driver and vehicle.');
      return;
    }

    const reviewData: ReviewRequest = {
      rideId: this.rideId,
      passengerId: this.passengerId,
      driverRating: this.driverRating(),
      vehicleRating: this.vehicleRating(),
      comment: this.comment()
    };

    // POZIV BACKEND-A
    this.rideService.rateRide(reviewData).subscribe({
      next: () => {
        this.toastr.success('Thank you for your feedback!');
        localStorage.removeItem('activeRideData');
        this.close.emit();
        this.router.navigate(['/map']);
      },
      error: (err) => {
        const errorMsg = err.error?.message || 'Failed to submit review.';
        this.toastr.error(errorMsg);
        console.error("Rating Error:", err);
      }
    });
  }

  onDismiss() {
    this.close.emit();
    this.router.navigate(['/map']);
  }
}