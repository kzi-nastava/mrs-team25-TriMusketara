import { Routes } from '@angular/router';
import { ChangeInfoPage } from './layout/change-info-page/change-info-page';
import { MapViewComponent } from './components/map-view/map-view';
import { DriverHistory } from './layout/driver-history/driver-history';
import { LoginPage } from './layout/login-page/login-page';
import { RegistrationPage } from './layout/registration-page/registration-page';
import { MainPageComponent } from './main-page/main-page';


export const routes: Routes = [
  { path: '', redirectTo: 'map', pathMatch: 'full' },
  { path: 'map', component: MainPageComponent },
  { path: 'change-information-page',component: ChangeInfoPage },
  { path: 'driver-history', component: DriverHistory },
  { path: 'scheduled-rides', component: MainPageComponent },
  { path: 'change-info', component: MapViewComponent },
  { path: 'reports', component: MapViewComponent },
  { path: 'notes', component: MapViewComponent },
  { path: 'support', component: MapViewComponent },
  { path: 'login', component: LoginPage },
  { path: 'register', component: RegistrationPage }
];
