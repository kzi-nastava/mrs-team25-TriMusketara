import { Component, inject, OnChanges , Output, EventEmitter, Input, SimpleChange } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service'; 
import { RideOrderingService } from '../../services/ride.service';
import { RideOrderCreate } from '../../services/models/ride-order-create';
import { Location } from '../../services/models/location';
import { ToastrService } from 'ngx-toastr';

interface FormField {
  label: string;
  text: string;
  type: string;
  placeholder: string;
  options: string[];
  required: boolean;
}

@Component({
  selector: 'app-ride-ordering',
  imports: [FormsModule],
  templateUrl: './ride-ordering.html',
  styleUrl: './ride-ordering.css',
})
export class RideOrdering implements OnChanges {
  //services
  // private authService = inject(AuthService);
  // private router = inject(Router);

  // Needed to get longitude and latitude coords from mainpage where we geolocate
  @Input() resolvedLocations?: {
    origin: Location;
    destination: Location;
    stops: Location[];
  };  

  @Input() routeInfo?: {
    durationMinutes: number;
    distanceKm: number;
  }

  private rideSubmitted = false;

  @Output()
  rideRequested = new EventEmitter<{
    origin: string;
    destination: string;
    stops: string[];
  }>

  @Output()
  closeRequested = new EventEmitter<void>();  

  constructor (private rideOrderService: RideOrderingService, private toastr: ToastrService) {}

  // These are static fields, we have one value and one input
  topFields = [
    {label: 'origin', text: 'Origin:', type: 'text', placeholder: 'Zabalj', required: true},
    {label: 'destination', text: 'Destination:', type: 'text', placeholder: 'Novi Sad', required: true}
  ];

  bottomFields = [
    {label: 'type', text: 'Vehicle type:', type: 'select', options: ['LUXURY', 'STANDARD', 'VAN'], required: true},
    {label: 'time', text: 'Set time:', type: 'time', placeholder: '', required: true},
    {label: 'baby-friendly', text: 'Baby friendly:', type: 'checkbox', placeholder: '', required: false},
    {label: 'pet-friendly', text: 'Pet friendly:', type: 'checkbox', placeholder: '', required: false}
  ]

  // Form data
  // Here we will save real data the user inputs into the form
  formData: any = {};

  // Error messages for each field, if that field is invalid
  fieldErrors: Record<string, string> = {};

  // These are dynamic fields, the user can add more values to these inputs
  additionalStops: string[] = ['']; // additional stops list
  linkedPassengers: string[] = ['']; // linked passengers list

  invalidFields: string[] = []; // List of invalid form fields

  // Control for when showing pop-ups for addition of additional stops, or linking other passenger emails
  showStopsModal = false;
  showPassengerModal = false;

  // ngOnInit is a method that is called when a component is loaded
  // Here we will initialize our form data with empty values which will later contain real values the user inputs
  ngOnInit() {
    const allFieldsCombined = [...this.topFields, ...this.bottomFields];

    for (let i = 0; i < allFieldsCombined.length; i++) {
      const field = allFieldsCombined[i];

      // If field is checkbox, the initial value should be false (it isnt checked)
      if (field.type === 'checkbox') {
        this.formData[field.label] = false;
      }
      else {
        this.formData[field.label] = '';
      }
    }
  }

  get allFields() {
    return [...this.topFields, ...this.bottomFields];
  }

  openStopsModal() {
    this.showStopsModal = true;
  }

  closeStopsModal() {
    this.showStopsModal = false;
  }

  addStop() {
    this.additionalStops.push('');
  }

  removeStop(index: number) {
    // Allow removal if there are more than one additional stop
    if (this.additionalStops.length > 1) {
      this.additionalStops.splice(index, 1);
    }
  }

  openPassengerModal() {
    this.showPassengerModal = true;
  }

  closePassengerModal() {
    this.showPassengerModal = false;
  }

  addPassenger() {
    this.linkedPassengers.push('');
  }

  removePassenger(index: number) {
    if (this.linkedPassengers.length > 1) {
      this.linkedPassengers.splice(index, 1);
    }
  }  

  // Validating a single field
  // Taking in the label of the input, and the value of the user, if everything seems okay we return true, else false
  validateField(label: string, value: any): boolean {
    // Find label
    const fieldDefinition = this.allFields.find(f => f.label === label);

    delete this.fieldErrors[label];

    if (fieldDefinition === undefined) {
      return true;
    }

    if (fieldDefinition.type === 'checkbox') return true;

    // Check if the field is required
    if (fieldDefinition.required === true) {
      // Required field cannot be empty, (origin, destination, time...)
      if (!value || value.toString().trim() === '') {
        this.fieldErrors[label] = 'This field is required';
        return false;
      }
    }

    // Origin and destination cannot be the same
    if (label === 'destination') {
      if (this.formData.origin && value && this.formData.origin.trim() === value.trim()) {
        this.fieldErrors[label] = 'Origin and destination locations must be different';
        return false;
      }
    }

    // Time validation 
    if (label === 'time' && value) {
      const now = new Date();
      const selectedTime = new Date();

      const [hours, minutes] = value.split(':').map(Number);
      selectedTime.setHours(hours, minutes, 0, 0);

      if (selectedTime <= now) {
        this.fieldErrors[label] = 'Time has passed';
        return false;
      }
    }
    return true;
  }

  // Validate the entire form now
  // Call the function above for all form fields
  validateForm(): boolean {
    this.invalidFields = [];
    this.fieldErrors = {};
    
    for (let i = 0; i < this.allFields.length; i++) {
        const field = this.allFields[i];

        // Get current value
        const currentValue = this.formData[field.label];

        const isValid = this.validateField(field.label, currentValue);

        if (isValid === false) {
          // Add field label to list o finvalid fields
          this.invalidFields.push(field.label);
        }
      }

      const invalidEmails = this.linkedPassengers.filter(email => email.trim() !== '' && !this.isValidEmail(email));

      if (invalidEmails.length > 0) {
        this.fieldErrors['linkedPassengers'] = 'One or more emails are invalid';
        return false;
      }

      // If the list is empty, all fields are valid
      if (this.invalidFields.length === 0) {
        return true;
      }
      else {
        return false;
      }
  }

  // Helper for email form validation
  isValidEmail(email: string): boolean {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
  }

  onFinishOrder() {
    const isFormValid = this.validateForm();

    if (isFormValid === false) {
      this.toastr.error('Please fill in all required fields correctly', 'Validation Error');
      return;
    }

    // Clear empty entries from dynamic fields (additional stops and passengers)
    const cleanedStops = this.additionalStops.filter(s => s.trim() !== '');
    this.linkedPassengers = this.linkedPassengers.filter(p => p.trim() !== '');

    // Get values from input fields
    const origin = this.formData.origin.trim();
    const destination = this.formData.destination.trim();

    // Show info message that we're processing the request
    this.toastr.info('Processing your ride request...', 'Please wait');

    // Emitting event to MainPage 
    this.rideRequested.emit({
      origin,
      destination,
      stops: cleanedStops
    });
  }

  ngOnChanges(changes: { [propName: string]: SimpleChange<any>; }): void {
    if (this.resolvedLocations && this.routeInfo && !this.rideSubmitted) {
      console.log("Both locations and route info available, building ride");

      this.rideSubmitted = true;

      const ride = this.buildRide();

      console.log('Final ride object:', ride);
      this.rideOrderService.createRide(ride).subscribe({
        next: () => {
          console.log("Ride created successfully");
          this.toastr.success('Your ride has been scheduled successfully', 'Success');
          this.rideSubmitted = false; 
          this.closeRequested.emit();
        },
        error: (err) => {
          console.error('Ride creation failed:', err);
          const errorMessage = err.error?.message || err.error || 'Failed to create ride. Please try again.';
          this.toastr.error(errorMessage, 'Error');
          this.rideSubmitted = false; 
        }
      });
    } else {
      console.log('Waiting for all data...', {
        hasLocations: !!this.resolvedLocations,
        hasRouteInfo: !!this.routeInfo,
        alreadySubmitted: this.rideSubmitted
      });
    }
  }

  // Build ride object to send
  buildRide(): RideOrderCreate {

    if (!this.resolvedLocations) {
      console.log('Resolved locations:', this.resolvedLocations);
      throw new Error("Locations not resolved");
    }

    return {
      origin: this.resolvedLocations.origin,
      destination: this.resolvedLocations.destination,
      stops: this.resolvedLocations.stops,
      passengerEmails: this.linkedPassengers.filter(p => p.trim() !== ''),
      vehicleType: this.formData['type'],
      scheduledTime: this.buildScheduledDateTime(this.formData['time']),
      babyFriendly: this.formData['baby-friendly'],
      petFriendly: this.formData['pet-friendly'],
      durationMinutes: this.routeInfo?.durationMinutes || 0,
      distanceKm: this.routeInfo?.distanceKm || 0
    };
  }

  // Helper function, because "time" returns HH:mm but we need to map it to backend LocalDateTime
  buildScheduledDateTime(time: string): string {
    const now = new Date();
    const [hours, minutes] = time.split(':').map(Number);
  
    const scheduled = new Date(
      now.getFullYear(),
      now.getMonth(),
      now.getDate(),
      hours,
      minutes,
      0,
      0
    );
  
    const pad = (n: number) => n.toString().padStart(2, '0');
  
    return `${scheduled.getFullYear()}-${pad(scheduled.getMonth() + 1)}-${pad(scheduled.getDate())}`
         + `T${pad(scheduled.getHours())}:${pad(scheduled.getMinutes())}:00`;
  }
  
  
  isFieldInvalid(label: string): boolean {
    return this.invalidFields.includes(label);
  }
}
