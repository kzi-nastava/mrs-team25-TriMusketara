import { Component } from '@angular/core';
import { UserService } from '../../services/user.service';
import { UserProfileInformation } from '../../services/models/user-profile-information';
import { ChangeDetectorRef } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { VehicleInformation } from '../../services/models/driver-vehicle-information';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ChangePassword } from '../change-password/change-password';

@Component({
  selector: 'app-change-info-page',
  imports: [FormsModule, CommonModule, ChangePassword],
  templateUrl: './change-info-page.html',
  styleUrl: './change-info-page.css',
})
export class ChangeInfoPage {

  // User fields
  fields: {
    label: string;
    text: string;
    type: string;
    key: keyof UserProfileInformation;
  }[] =[
    {label: 'fname', text: 'First name:', type: 'text', key: 'name'},
    {label: 'lname', text: 'Last name:', type: 'text', key: 'surname'},
    {label: 'email', text: 'Email:', type: 'email', key: 'email'},
    {label: 'address', text: 'Address:', type: 'text', key: 'address'},
    {label: 'mobile', text: 'Mobile:', type: 'text', key: 'phone'},
  ];

  // Vehicle fields
  vehicleFields: {
    label: string;
    text: string;
    type: string;
    options?: string[];
    key: keyof VehicleInformation;
  } [] = [
    {label: 'model', text: 'Model:', type: 'text', key: 'model'},
    {label: 'type', text: 'Type:', type: 'select', options: ['LUXURY', 'STANDARD', 'VAN'], key: 'type'},
    {label: 'licence', text: 'Plate number:', type: 'text', key: 'registration'},
    {label: 'baby-friendly', text: 'Baby friendly:', type: 'checkbox', key: 'isBabyFriendly'},
    {label: 'pet-friendly', text: 'Pet friendly:', type: 'checkbox', key: 'isPetFriendly'},
  ]

  originalProfile: UserProfileInformation | null = null;
  originalVehicle: VehicleInformation | null = null;

  fieldErrors: Record<string, string>= {};
  invalidFields: string[] = [];

  userForm: any = {};
  vehicleForm: any = {};

  isDriver = false; // Flag so if the driver is logged in, we allow viewing of vehicle info
  showVehicleForm = false;

  userId!: number;
  showChangePasswordModal = false;

  constructor(
    private userService: UserService, 
    private cdr: ChangeDetectorRef, 
    private toastr: ToastrService, 
    private router: Router,
    private authService: AuthService
    ) {}

  ngOnInit(): void {
    const userIdFromToken = this.authService.getUserIdFromToken();
    console.log('User ID from token:', userIdFromToken);
    if (userIdFromToken) {
      this.userId = userIdFromToken;
    } else {
      this.toastr.error('User not authenticated', 'Error');
      this.router.navigate(['/login']);
      return;
    }
    this.isDriver = this.authService.getRoleFromToken() === 'Driver';

    // Load users information
    this.userService.getUserProfileInfo(this.userId).subscribe({
      next: (res) => {
          this.originalProfile = { ...res };
          this.userForm = { ...res };
          this.cdr.detectChanges(); // change detection
      },
      error: (err) => {
        console.log(err);
      }
    });

    // If the user is driver, load vehicle information too
    if (this.isDriver) {
      this.userService.getDriverVehicle(this.userId).subscribe({
        next: (res) => {
            this.originalVehicle = { ... res };
            this.vehicleForm = { ...res };
            console.log(this.originalVehicle);
            this.cdr.detectChanges();
            console.log(this.originalProfile?.vehicle);
        },
        error: (err) => {
          console.log('Error loading vehicle info:', err);
        }
      });
    }
  }

  // If changing between forms
  toggleForm() {
    this.showVehicleForm = !this.showVehicleForm;
  }

  onSaveChanges() {
    if (!this.originalProfile) return;

    // Create new user object
    // const updatedProfile: UserProfileInformation = {
    //   id: this.originalProfile.id,
    //   name: (document.getElementById('fname') as HTMLInputElement).value,
    //   surname: (document.getElementById('lname') as HTMLInputElement).value,
    //   email: (document.getElementById('email') as HTMLInputElement).value,
    //   address: (document.getElementById('address') as HTMLInputElement).value,
    //   phone: (document.getElementById('mobile') as HTMLInputElement).value,
    // };

    const updatedProfile: UserProfileInformation = {
      id: this.originalProfile.id,
      name: this.userForm.name,
      surname: this.userForm.surname,
      email: this.userForm.email,
      address: this.userForm.address,
      phone: this.userForm.phone,
      blocked: false,
      blockReason: ''
    };

    let updatedVehicle: VehicleInformation | undefined;

    // If the user is a driver, add vehicle information
    if (this.isDriver && this.originalVehicle) {

      // const updatedVehicle: VehicleInformation = {
      //   id: this.originalVehicle.id,
      //   model: (document.getElementById('model') as HTMLInputElement).value,
      //   vehicleType: (document.getElementById('type') as HTMLSelectElement).value as VehicleInformation['vehicleType'],
      //   registration: (document.getElementById('licence') as HTMLInputElement).value,
      //   isBabyFriendly: (document.getElementById('baby-friendly') as HTMLInputElement).checked,
      //   isPetFriendly: (document.getElementById('pet-friendly') as HTMLInputElement).checked,
      // };

        updatedVehicle  = {
        id: this.originalVehicle.id,
        // Direct from form
        model: this.vehicleForm.model,
        type: this.vehicleForm.type,
        registration: this.vehicleForm.registration,
        isBabyFriendly: this.vehicleForm.isBabyFriendly,
        isPetFriendly: this.vehicleForm.isPetFriendly
      };

      console.log('Vehicle Object for validation:', updatedVehicle);
    }

    // Reset errors
    this.fieldErrors = {};
    this.invalidFields = [];

    // User form validation
    const isUserValid = this.validateForm(updatedProfile);
    if (!isUserValid) {
      this.toastr.error('User form is invalid', 'Error');
      this.showVehicleForm = false; 
      return;
    }

    // Vehicle form validation
    let isVehicleValid = true;
    if (this.isDriver && updatedVehicle) {
        isVehicleValid = this.validateVehicleForm(updatedVehicle);
        if (!isVehicleValid) {
          console.log('Vehicle validation failed. Errors:', this.fieldErrors);
          this.toastr.error('Vehicle form is invalid', 'Error');
          this.showVehicleForm = true; // show form to show errors
          return;
        }
    }

    // See if any data has been changed, if not notify
    const userChanged = this.hasProfileChanged(this.originalProfile, updatedProfile);
    const vehicleChanged = this.isDriver && updatedVehicle && this.originalVehicle ? this.hasVehicleChanged(this.originalVehicle, updatedVehicle) : false;

    
    console.log('User changed:', userChanged);
    console.log('Vehicle changed:', vehicleChanged);

    if (!userChanged && !vehicleChanged) {
      this.toastr.info('No changes detected', 'Info');
      return;
    }

    // Add vehicle to updatedprofile before sending
    if (updatedVehicle) {
      updatedProfile.vehicle = updatedVehicle;
    }

    // Send to backend
    // ...
    this.userService.changeUserInfo(this.userId, updatedProfile).subscribe({
      next: (res) => {
        console.log("Updated res" + res);
        this.fieldErrors = {};
        this.invalidFields = [];
        this.toastr.success('Changed information successfully', 'Success');
        //this.authService.logout();
        this.router.navigate(['/map']);
      },
      error: (err) => {
        const errorMessage = err.error?.message || err.error || 'There was an error changing information';
        this.toastr.error(errorMessage, 'Error');
        console.log(err);
      }
    })
  }

  // Validate single field - user form
  validateField(label: string, value:string): boolean {
    // Remove previous error for this field
    delete this.fieldErrors[label];

    if (!value || value.trim() === '') {
      this.fieldErrors[label] = 'This field is required';
      return false;
    }

    // Name validation
    if (label === 'fname') {
      if (value.length > 20) {
        this.fieldErrors[label] = 'First name must be maximum 20 characters';
        return false;
      }
      if (!this.isValidName(value)) {
        this.fieldErrors[label] = 'First name contains invalid characters';
        return false;
      }
    }

    // Last name validation
    if (label === 'lname') {
      if(value.length > 30) {
        this.fieldErrors[label] = 'Last name must be maximum 30 characters';
        return false;
      }
      if (!this.isValidName(value)) {
        this.fieldErrors[label] = 'Last name contains invalid characters';
        return false;
      }
    }

    // Email validation
    if (label === 'email') {
      if(!this.isValidEmail(value)) {
        this.fieldErrors[label] = 'Invalid email format';
        return false;
      }
    }

    // Address validation
    if (label === 'address') {
      if (value.length > 35) {
        this.fieldErrors[label] = 'Address must be maximum 35 characters';
        return false;
      }
    }

    if (label === 'mobile') {
      if (!this.isValidMobile(value)) {
        this.fieldErrors[label] = 'Invalid mobile format';
        return false;
      }
    }

    return true;
  }

  // Validate single field - vehicle form
  validateVehicleField(label: string, value: string): boolean {

    if (label == 'model') {
      if (!value || value.toString().trim() === '') {
        this.fieldErrors[label] = 'Vehicle model is required';
        return false;
      }
    }

    if (label === 'type') {
      if (!value || value.toString().trim() === '') {
        this.fieldErrors[label] = 'Vehicle type is reuqired';
        return false;
      }
    }

    if (label === 'licence') {
      if (!value || value.toString().trim() === '') {
        this.fieldErrors[label] = 'Registration table is required';
        return false;
      }
    }

    return true;
  }


  // Validate user form
  validateForm(updatedProfile: UserProfileInformation): boolean {
    this.invalidFields = [];
    this.fieldErrors = {};

    const validations = [
      { label: 'fname', value: updatedProfile.name },
      { label: 'lname', value: updatedProfile.surname },
      { label: 'email', value: updatedProfile.email },
      { label: 'address', value: updatedProfile.address },
      { label: 'mobile', value: updatedProfile.phone }
    ];

    for (const validation of validations) {
      const isValid = this.validateField(validation.label, validation.value);
      if (!isValid) {
        this.invalidFields.push(validation.label);
      }
    }

    return this.invalidFields.length === 0;
  }

  // Validate vehicle form
  validateVehicleForm(updatedVehicle: VehicleInformation): boolean {
    const validations = [
      { label: 'licence', value: updatedVehicle.registration },
      { label: 'model', value: updatedVehicle.model },
      { label: 'type', value: updatedVehicle.type }
    ];

    for (const validation of validations) {
      const isValid = this.validateVehicleField(validation.label, validation.value);
      if (!isValid) {
        console.warn(`Validation failed for field: ${validation.label}, value: "${validation.value}"`);
        this.invalidFields.push(validation.label);
      }
    }

    return this.invalidFields.length === 0;
  }

  // Check is user profile has changed
  hasProfileChanged(
    original: UserProfileInformation,
    updated: UserProfileInformation
  ): boolean {
    return (
      original.name !== updated.name ||
      original.surname !== updated.surname ||
      original.email !== updated.email ||
      original.address !== updated.address ||
      original.phone !== updated.phone
    );
  }

  // Check if vehicle has changed
  hasVehicleChanged(
    original: VehicleInformation,
    updated: VehicleInformation
  ): boolean {
    return (
      original.model !== updated.model ||
      original.registration !== updated.registration ||
      original.type !== updated.type ||
      original.isBabyFriendly !== updated.isBabyFriendly ||
      original.isPetFriendly !== updated.isPetFriendly
    );
  }

  // Helper metods
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
  
  isFieldInvalid(label: string): boolean {
    return this.invalidFields.includes(label);
  }

  openChangePasswordModal(): void {
    console.log('Opening modal...'); 
    this.showChangePasswordModal = true;
    console.log('Modal state:', this.showChangePasswordModal);
  }

  closeChangePasswordModal(): void {
    console.log('Closing modal...')
    this.showChangePasswordModal = false;
  }
}
