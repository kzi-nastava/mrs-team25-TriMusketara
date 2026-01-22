import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ProfileSidebarService } from '../../services/profile-sidebar.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-driver-registration',
  imports: [FormsModule],
  templateUrl: './driver-registration.html',
  styleUrl: './driver-registration.css',
})
export class DriverRegistration {
  driverFields = [
    {label: 'fname', text: 'First name:', type: 'text', placeholder: 'Jane', required: true},
    {label: 'lname', text: 'Last name:', type: 'text', placeholder: 'Doe', required: true},
    {label: 'email', text: 'Email:', type: 'email', placeholder: 'janedoe@gmail.com', required: true},
    {label: 'address', text: 'Address:', type: 'text', placeholder: 'Las Noches, Hueco Mundo', required: true},
    {label: 'mobile', text: 'Mobile:', type: 'text', placeholder: '123456789', required: true},
  ];

  vehicleFields = [
    {label: 'model', text: 'Model:', type: 'text', placeholder: 'Model', required: true},
    {label: 'type', text: 'Type:', type: 'select', options: ['Luxury', 'Standard', 'Van'], required: true},
    {label: 'licence', text: 'Plate number:', type: 'text', placeholder: 'NS319KK', required: true},
    {label: 'seats', text: 'Num. of seats:', type: 'number', placeholder: '', min: 4, max: 12, required: true},
    {label: 'baby-friendly', text: 'Baby friendly:', type: 'checkbox', placeholder: '', required: false},
    {label: 'pet-friendly', text: 'Pet friendly:', type: 'checkbox', placeholder: '', required: false},
  ];

  public constructor(private router: Router, public profileSidebar: ProfileSidebarService) {}

  formData: Record<string, any> = {};

  fieldErrors: Record<string, string> = {};

  invalidFields: string[] = []; // List of invalid form fields

  showVehicleInfo = false; // This flag is used for page responsiveness 768px and 480px 
  goNext() {
    this.showVehicleInfo = true; // When the 'next' button is pressed in the form, continue to register the drivers vehicle
  }
  goBack() {
    this.showVehicleInfo = false; // When the 'back' button is pressed in the form, return to driver information form
  }

  ngOnInit() {
    const allFieldsCombined = [...this.driverFields, ...this.vehicleFields];

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

    // Name and last name can only be letters
    if ((label === 'fname' || label === 'lname') && value) {
      if (!this.isValidName(value)) {
        this.fieldErrors[label] = 'Field should contain letters only';
        return false;
      } 
    }

    // Mobile
    if (label === 'mobile' && value) {
      if (!this.isValidMobile(value)) {
        this.fieldErrors[label] = 'Inavlid mobile format';
        return false;
      }
    }

    // Email format validation
    if (label === 'email' && value) {
      if (!this.isValidEmail(value)) {
        this.fieldErrors[label] = 'Invalid email format';
        return false;
      }
    }

    // Number of seats
    if (label === 'seats' && value) {
      const seats = Number(value);
      if (seats < 4 || seats > 12) {
        this.fieldErrors[label] = 'Seats must be between 4 and 12';
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

      // If the list is empty, all fields are valid
      if (this.invalidFields.length === 0) {
        return true;
      }
      else {
        return false;
      }
  }

  
  // Helper for names validation
  isValidName(email: string): boolean {
    const regex = /^[A-Za-zČĆŽŠĐčćžšđ]+(?:[ -][A-Za-zČĆŽŠĐčćžšđ]+)*$/;
    return regex.test(email);
  }

  // Helper for mobile validation
  isValidMobile(email: string): boolean {
    const regex = /^(?:\+381|381|0)6\d{7,8}$/;
    return regex.test(email);
  }

  // Helper for email form validation
  isValidEmail(email: string): boolean {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
  }

  get allFields() {
    return [...this.driverFields, ...this.vehicleFields];
  }

  // Take form inputs and register a new driver
  registerDriver() {
    const isFormValid = this.validateForm();

    if (isFormValid === false) {
      return;
    }

    console.log('Driver data:', this.formData);
  }

  isFieldInvalid(label: string): boolean {
    return this.invalidFields.includes(label);
  }

   // When canceling driver registration go to main page , but also open the profile sidebar
   closeRegistrationForm() {
    this.router.navigate(['/']).then(() => {
      this.profileSidebar.open();
    })
  }

}
