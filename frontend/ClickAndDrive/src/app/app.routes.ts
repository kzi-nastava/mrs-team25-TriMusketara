import { Routes } from '@angular/router';
import { MapViewComponent } from './components/map-view/map-view';
import { DriverHistory } from './layout/driver-history/driver-history';


export const routes: Routes = [
  { path: '', redirectTo: 'map', pathMatch: 'full' },
  { path: 'map', component: MapViewComponent },
  { path: 'driver-history', component: DriverHistory },
  { path: 'scheduled-rides', component: MapViewComponent },
  { path: 'change-info', component: MapViewComponent },
  { path: 'reports', component: MapViewComponent },
  { path: 'notes', component: MapViewComponent },
  { path: 'support', component: MapViewComponent },
  //{ path: 'login', component: LoginPage },
];
