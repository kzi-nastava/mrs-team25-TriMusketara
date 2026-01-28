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
import { CompleteDriverRegistration } from './layout/complete-driver-registration/complete-driver-registration';
import { ActivateAccount } from './layout/activate-account/activate-account';
import { AuthGuard } from './services/auth.guard';


export const routes: Routes = [
   { path: '', redirectTo: 'map', pathMatch: 'full' },
  { path: 'map', component: MainPageComponent },

  // USER ROUTES
  { path: 'change-info', component: MapViewComponent, canActivate: [AuthGuard], data: { role: 'user' } },
  { path: 'favorite-routes', component: FavoriteRoutes, canActivate: [AuthGuard], data: { role: 'user' } },
  { path: 'rate-ride', component: RideRating, canActivate: [AuthGuard], data: { role: 'user' } },

  // DRIVER ROUTES
  { path: 'driver-history', component: DriverHistory, canActivate: [AuthGuard], data: { role: 'driver' } },
  { path: 'scheduled-rides', component: ScheduledRides, canActivate: [AuthGuard], data: { role: 'driver' } },

  // ADMIN ROUTES
  { path: 'reports', component: MapViewComponent, canActivate: [AuthGuard], data: { role: 'admin' } },
  { path: 'notes', component: MapViewComponent, canActivate: [AuthGuard], data: { role: 'admin' } },
  { path: 'support', component: MapViewComponent, canActivate: [AuthGuard], data: { role: 'admin' } },
  { path: 'driver-registration', component: DriverRegistration, canActivate: [AuthGuard], data: { role: 'admin' } },

  // PUBLIC ROUTES
  { path: 'login', component: LoginPage },
  { path: 'register', component: RegistrationPage },
  { path: 'change-information-page', component: ChangeInfoPage },
  { path: 'complete-registration', component: CompleteDriverRegistration },
  { path: 'activate-account', component: ActivateAccount },

  //SHARED ROUTES
  { path: 'drive-in-progress', component: DriveInProgress, canActivate: [AuthGuard], data: { roles: ['user', 'driver'] } },

  // CATCH ALL
  { path: '**', redirectTo: 'map' }
];
