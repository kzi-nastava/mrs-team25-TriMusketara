import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service'; 

@Component({
  selector: 'app-ride-ordering',
  imports: [FormsModule],
  templateUrl: './ride-ordering.html',
  styleUrl: './ride-ordering.css',
})
export class RideOrdering {
  //services
  private authService = inject(AuthService);
  private router = inject(Router);

  // These are static fields, we have one value and one input
  topFields = [
    {label: 'origin', text: 'Origin:', type: 'text', placeholder: 'Zabalj', required: true},
    {label: 'destination', text: 'Destination:', type: 'text', placeholder: 'Novi Sad', required: true}
  ];

  bottomFields = [
    {label: 'type', text: 'Vehicle type:', type: 'select', options: ['Luxury', 'Standard', 'Van'], required: true},
    {label: 'time', text: 'Set time:', type: 'time', placeholder: '', required: true},
    {label: 'baby-friendly', text: 'Baby friendly:', type: 'checkbox', placeholder: '', required: false},
    {label: 'pet-friendly', text: 'Pet friendly:', type: 'checkbox', placeholder: '', required: false}
  ]

  showStopsModal = false;
  showPassengerModal = false;

  openStopsModal() {
    this.showStopsModal = true;
  }

  closeStopsModal() {
    this.showStopsModal = false;
  }

  openPassengerModal() {
    this.showPassengerModal = true;
  }

  closePassengerModal() {
    this.showPassengerModal = false;
  }

  // These are dynamic fields, the user can add more values to these inputs
  additionalStops: string[] = ['']; // additional stops list
  linkedPassengers: string[] = ['']; // linked passengers list

  invalidFields: string[] = []; // List of invalid form fields

  addStop() {
    this.additionalStops.push('');
  }

  removeStop(index: number) {
    if (this.additionalStops.length > 1) {
      this.additionalStops.splice(index, 1);
    }
  }

  addPassenger() {
    this.linkedPassengers.push('');
  }

  removePassenger(index: number) {
    if (this.linkedPassengers.length > 1) {
      this.linkedPassengers.splice(index, 1);
    }
  }

  get allFields() {
    return [...this.topFields, ...this.bottomFields];
  }  

  onFinishOrder() {
  this.invalidFields = []; 

  // Validation logic
  this.allFields.forEach(field => {
    if (field.required) {
      const element = document.getElementById(field.label) as HTMLInputElement | HTMLSelectElement;

      if (field.type === 'checkbox') {
        const checkbox = element as HTMLInputElement;
        if (!checkbox.checked) {
          this.invalidFields.push(field.label);
        }
      } else {
        if (!element || !element.value || element.value.trim() === '') {
          this.invalidFields.push(field.label);
        }
      }
    }
  });

  // If there are invalid fields, stop the method here (do not navigate)
  if (this.invalidFields.length > 0) {
    console.log("Validation failed for fields:", this.invalidFields);
    return; 
  }

  // If validation passed (invalidFields is empty), continue:
  console.log("Validation passed! Ordering ride...");

  // Clear empty entries from dynamic fields (additional stops and passengers)
  this.additionalStops = this.additionalStops.filter(s => s.trim() !== '');
  this.linkedPassengers = this.linkedPassengers.filter(p => p.trim() !== '');

  // Activating the driving state in the service
  this.authService.setInDrive(true);

  // Navigating to the drive-in-progress page
  this.router.navigate(['/drive-in-progress']);
}

  isFieldInvalid(label: string): boolean {
    return this.invalidFields.includes(label);
  }
}
