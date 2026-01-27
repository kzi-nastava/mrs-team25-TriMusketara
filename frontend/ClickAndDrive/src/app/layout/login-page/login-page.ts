import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClientModule, HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './login-page.html',
  styleUrls: ['./login-page.css'],
})
export class LoginPage {
  email = '';
  password = '';

  constructor(
    private router: Router,
    private http: HttpClient,
    private authService: AuthService
  ) {}

  goHome() {
    this.router.navigate(['/map']);
  }

  login() {
    const headers = new HttpHeaders({ 'skip': 'true' });
    
    this.http.post('http://localhost:8080/api/user/auth/login', {
      email: this.email,
      password: this.password
    }, {headers}).subscribe({
      next: (res: any) => {
        if (!res.token) {
          return;
        }

        localStorage.setItem('token', res.token);
        this.authService.setUserType(res.role);
        this.authService.setUsername(res.email);

        this.router.navigate(['/']);  // redirect na početnu
      },
      error: (err) => {
        console.error('Login error:', err);
        console.log('Login failed: ' + (err.error?.message || 'Unknown error'));
      }
    });
  }
}
