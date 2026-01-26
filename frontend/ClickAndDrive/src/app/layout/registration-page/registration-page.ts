import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RegisterRequest } from '../../services/models/register-request';

@Component({
  selector: 'app-registration-page',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './registration-page.html',
  styleUrl: './registration-page.css',
})
export class RegistrationPage {

  registerData: RegisterRequest = {
    name: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    address: '',
    phoneNumber: ''
  };

  constructor(
    private router: Router,
    private http: HttpClient
  ) {}

  goHome() {
    this.router.navigate(['/']);
  }

  register(event: Event) {
    const form = event.target as HTMLFormElement;

    if (!form.checkValidity()) {
      form.reportValidity();
      return;
    }

    this.http.post(
      'http://localhost:8080/api/user/auth/register',
      this.registerData
    ).subscribe({
      next: () => this.router.navigate(['/login']),
      error: (err) => console.error('Registration error:', err)
    });
  }

  checkPasswords(input: HTMLInputElement) {
    if (this.registerData.password !== this.registerData.confirmPassword) {
      input.setCustomValidity('Passwords must match');
    } else {
      input.setCustomValidity('');
    }
  }
}
