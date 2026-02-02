import { Component } from '@angular/core';
import { DriverService } from '../../services/driver.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CompleteRegistration } from '../../services/models/complete-driver-registration';
import { ToastrService } from 'ngx-toastr';


@Component({
  selector: 'app-complete-driver-registration',
  imports: [FormsModule, CommonModule],
  templateUrl: './complete-driver-registration.html',
  styleUrl: './complete-driver-registration.css',
})
export class CompleteDriverRegistration {

  constructor (private route: ActivatedRoute, private router: Router, private driverService: DriverService, private toastr: ToastrService) {}

  token: string = '';
  password: string = '';
  confirmPassword: string = '';

  isLoading: boolean = false;
  errorMessage: string = '';
  fieldErrors: Record<string, string> = {};

  ngOnInit(): void {
    // Take token 
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];

      if (!this.token) {
        this.errorMessage = "Token is missing from URL";
      }
    });
  }

  // Single field validation
  validateField(fieldName: string, value: string): boolean {
    delete this.fieldErrors[fieldName];

    // Is empty
    if (!value || value.trim() === '') {
      this.fieldErrors[fieldName] = 'This field is required';
      return false;
    }

    // Password validation
    if (fieldName === 'password') {
      if (value.length < 8) {
        this.fieldErrors[fieldName] = 'Password must be at least 8 characters';
        return false;
      }

      // Check if password has at least an upper letter, a number
      const hasUpperCase = /[A-Z]/.test(value);
      const hasNumber = /[0-9]/.test(value);

      if (!hasUpperCase || !hasNumber) {
        this.fieldErrors[fieldName] = 'Password must contain at least one uppercase letter and one number';
        return false;
      }
    }

    // Confirm password validation
    if (fieldName === 'confirmPassword') {
      if (value != this.password) {
        this.fieldErrors[fieldName] = "Passwords do not match";
        return false;
      }
    }

    return true;
  }

  // Validate entire form
  validateForm(): boolean {
    this.fieldErrors = {};

    const isPasswordValid = this.validateField('password', this.password);
    const isConfirmValid = this.validateField('confirmPassword', this.confirmPassword);

    return isPasswordValid && isConfirmValid;
  }

  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const request: CompleteRegistration = {
      token: this.token,
      password: this.password,
      confirmPassword: this.confirmPassword
    };

    this.driverService.completeRegistration(request).subscribe({
      next: () => {
        this.toastr.success('Registration completed successfully', 'Success');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Registration error:', err);
        this.toastr.error(err, 'Error');
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
    })
  }

  isFieldInvalid(fieldName: string): boolean {
    return !!this.fieldErrors[fieldName];
  }
}
