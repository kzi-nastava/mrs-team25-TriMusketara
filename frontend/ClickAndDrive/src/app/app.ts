import { Component, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar';
import { MainPageComponent } from './main-page/main-page';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, MainPageComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})

export class App {
  protected readonly title = 'ClickAndDrive';

  constructor(public router: Router) {}

  showNavbar(): boolean {
    return this.router.url !== '/login' && this.router.url !== '/register'
          && this.router.url !== '/driver-registration';
  }
}