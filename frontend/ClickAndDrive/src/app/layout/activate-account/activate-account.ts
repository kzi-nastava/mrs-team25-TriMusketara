import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-activate-account',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  template: `
    <h2>{{ message }}</h2>
  `
})
export class ActivateAccount implements OnInit {

  message = 'Activating account...';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.message = 'Invalid activation link.';
      return;
    }

    this.http.get(
      `http://localhost:8080/api/user/auth/activate/${token}`
    ).subscribe({
      next: () => {
        this.message = 'Account activated. You can now log in.';
        setTimeout(() => this.router.navigate(['/login']), 2500);
      },
      error: () => {
        this.message = 'Activation link is invalid or expired.';
      }
    });
  }
}
