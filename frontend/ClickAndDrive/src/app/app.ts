import { Component, signal } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar';
import { MainPageComponent } from './main-page/main-page';
import { LoginPage } from './layout/login-page/login-page';

const routes: Routes = [
  { path: '', component: MainPageComponent },
  { path: 'login', component: LoginPage },
];

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule],
  template: `
    <router-outlet></router-outlet>
  `,
  styleUrls: ['./app.css']
})
  
export class App {
  protected readonly title = signal('ClickAndDrive');
}