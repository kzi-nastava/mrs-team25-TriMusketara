import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../services/auth.service';
import { ChangePasswordRequest } from '../../services/models/change-password';
import { Router } from '@angular/router';

@Component({
  selector: 'app-change-password',
  imports: [FormsModule, CommonModule],
  templateUrl: './change-password.html',
  styleUrl: './change-password.css',
})
export class ChangePassword {
  @Output() close = new EventEmitter<void>();
  
  currentPassword: string = '';
  newPassword: string = '';
  confirmPassword: string = '';

  isLoading: boolean = false;
  fieldErrors: Record<string, string> = {};

  constructor(
    private userService: UserService,
    private toastr: ToastrService,
    private authService: AuthService,
    private router: Router
  ) {}

  validateField(fieldName: string, value: string): boolean {
    delete this.fieldErrors[fieldName];

    if (!value || value.trim() === '') {
      this.fieldErrors[fieldName] = 'This field is required';
      return false;
    }

    if (fieldName === 'currentPassword') {
      if (value.length < 8) {
        this.fieldErrors[fieldName] = 'Password must be at least 8 characters';
        return false;
      }
    }

    if (fieldName === 'newPassword') {
      if (value.length < 8) {
        this.fieldErrors[fieldName] = 'Password must be at least 8 characters';
        return false;
      }

      const hasUpperCase = /[A-Z]/.test(value);
      const hasNumber = /[0-9]/.test(value);

      if (!hasUpperCase || !hasNumber) {
        this.fieldErrors[fieldName] = 'Password must contain at least one uppercase letter and one number';
        return false;
      }
    }

    if (fieldName === 'confirmPassword') {
      if (value !== this.newPassword) {
        this.fieldErrors[fieldName] = "Passwords do not match";
        return false;
      }
    }

    return true;
  }

  validateForm(): boolean {
    this.fieldErrors = {};

    const isCurrentValid = this.validateField('currentPassword', this.currentPassword);
    const isNewValid = this.validateField('newPassword', this.newPassword);
    const isConfirmValid = this.validateField('confirmPassword', this.confirmPassword);

    return isCurrentValid && isNewValid && isConfirmValid;
  }

  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }
  
    const userId = this.authService.getUserIdFromToken();
    if (!userId) {
      this.toastr.error('User not authenticated', 'Error');
      return;
    }
  
    this.isLoading = true;
  
    const request: ChangePasswordRequest = {
      id: userId,
      currentPassword: this.currentPassword,
      newPassword: this.newPassword,
      confirmPassword: this.confirmPassword
    };
  
    this.userService.changePassword(request).subscribe({
      next: (res) => {
        this.handleSuccess();
      },
      error: (err) => {
        if (err.status === 200) {
            this.handleSuccess();
            return;
        }

        const errorMessage = err.error?.message || err.error || 'Error changing password';
        this.toastr.error(errorMessage, 'Error');
        
        setTimeout(() => {
             this.isLoading = false; 
        });
      }
    });
  }

  private handleSuccess() {
      this.toastr.success('Password changed successfully', 'Success');

      setTimeout(() => {
        this.isLoading = false;
        this.close.emit();
        this.authService.logout();
        this.router.navigate(['/login']);
      }, 0);
  }

  isFieldInvalid(fieldName: string): boolean {
    return !!this.fieldErrors[fieldName];
  }

  onClose(): void {
    this.close.emit();
  }
}