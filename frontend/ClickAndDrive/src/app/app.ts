import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NavbarComponent, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})

export class App {
  protected readonly title = 'ClickAndDrive';

  constructor(public router: Router) {}

  showNavbar(): boolean {
    return this.router.url !== '/login' && this.router.url !== '/register';
  }
}