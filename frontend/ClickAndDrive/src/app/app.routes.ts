import { Routes } from '@angular/router';
import { ChangeInfoPage } from './layout/change-info-page/change-info-page';
import { MapViewComponent } from './components/map-view/map-view';
import { DriverHistory } from './layout/driver-history/driver-history';
import { LoginPage } from './layout/login-page/login-page';
import { RegistrationPage } from './layout/registration-page/registration-page';
import { ScheduledRides } from './layout/scheduled-rides/scheduled-rides';
import { DriveInProgress } from './layout/drive-in-progress/drive-in-progress';
import { MainPageComponent } from './main-page/main-page';
import { DriverRegistration } from './layout/driver-registration/driver-registration';
import { FavoriteRoutes } from './layout/favorite-routes/favorite-routes';
import { RideRating } from './layout/ride-rating/ride-rating';


export const routes: Routes = [
  { path: '', redirectTo: 'map', pathMatch: 'full' },
  { path: 'map', component: MainPageComponent},
  { path: 'change-information-page',component: ChangeInfoPage },
  { path: 'driver-history', component: DriverHistory },
  { path: 'scheduled-rides', component: ScheduledRides },
  { path: 'change-info', component: MapViewComponent },
  { path: 'reports', component: MapViewComponent },
  { path: 'notes', component: MapViewComponent },
  { path: 'support', component: MapViewComponent },
  { path: 'login', component: LoginPage },
  { path: 'register', component: RegistrationPage },
  { path: 'drive-in-progress', component: DriveInProgress },
  { path: 'driver-registration', component: DriverRegistration },
  { path: 'favorite-routes', component: FavoriteRoutes },
  { path: 'rate-ride', component: RideRating}
];
