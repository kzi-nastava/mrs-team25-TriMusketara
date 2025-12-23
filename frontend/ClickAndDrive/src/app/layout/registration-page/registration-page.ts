import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registration-page',
  standalone: true,
  imports: [],
  templateUrl: './registration-page.html',
  styleUrl: './registration-page.css',
})

export class RegistrationPage {
  constructor(private router: Router) {}

  goHome() {
    this.router.navigate(['/']);
  }
}