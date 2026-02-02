import { ChangeDetectorRef, Component } from '@angular/core';
import { FavoriteToggle } from '../../components/favorite-toggle/favorite-toggle';
import { RouteFromFavorites } from '../../services/models/route-from-favorites';
import { PassengerService } from '../../services/passenger.service';
import { AuthService } from '../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { SharedRideDataService } from '../../services/shared-ride-data.service';


/* RouteDTO for dummy data - for now */
interface RouteDTO {
  id: number;
  from: string;
  to: string;
  favorite: boolean;
  distance: number;
  duration: number;
  timesUsed: number;
}

@Component({
  selector: 'app-favorite-routes',
  imports: [FavoriteToggle],
  templateUrl: './favorite-routes.html',
  styleUrl: './favorite-routes.css',
})
export class FavoriteRoutes {

  favoriteRoutes: RouteFromFavorites[] = [];

  constructor(private passengerService: PassengerService, private cdr: ChangeDetectorRef, private authService: AuthService, private toastr: ToastrService, private router: Router, private sharedRideDataService: SharedRideDataService) {}

  routes: RouteDTO[] = [
    { id: 1, from: 'Beograd', to: 'Novi Sad', favorite: true, distance: 96, duration: 70, timesUsed: 4},
    { id: 2, from: 'Nis', to: 'Beograd', favorite: true, distance: 235, duration: 130, timesUsed: 3 },
    { id: 3, from: 'Subotica', to: 'Novi Sad', favorite: true, distance: 106, duration: 85, timesUsed: 2},
  ];

  userId!: number;

  ngOnInit(): void {
    const userIdFromToken = this.authService.getUserIdFromToken();
    console.log('User ID from token:', userIdFromToken);
    if (userIdFromToken) {
      this.userId = userIdFromToken;
    } else {
      this.toastr.error('User not authenticated', 'Error');
      this.router.navigate(['/login']);
      return;
    }

    this.passengerService.getFavoriteRoutes(this.userId).subscribe({
      next: (routes) => {
        console.log('Routes received:', routes);
        this.favoriteRoutes = routes.map(r => ({ ...r, favorite: true }));
        if (this.favoriteRoutes.length === 0) {
          this.toastr.info('Add some routes to favorite', 'Info');
        }
        else {
          this.toastr.success('Favorite routes loaded', 'Success');
        }
        this.cdr.detectChanges();
        this.cdr.markForCheck(); 
      },
      error: (err) => {
        this.toastr.error(err, 'Error');
      }
    })
  }

  // Click on the heart button (remove from favorites)
  onFavoriteToggle(route: RouteFromFavorites, active:boolean) {
    route.favorite = active;
    console.log('Toggled favorite for route ID:', route.id);
    this.passengerService.removeFavoriteRoute(this.userId, route.id).subscribe({
        next: () => {
          this.toastr.info("Route successfully removed from favorites", "Info");
          this.favoriteRoutes = this.favoriteRoutes.filter(r => r.id !== route.id); // remove from array
          this.cdr.detectChanges();
          this.cdr.markForCheck(); 
        },
        error: (err) => {
          route.favorite = !active;
          this.toastr.error(err, "Error");
        }
    });
  }

  // Order favorite route
  onOrderClick(route: RouteFromFavorites) {
    console.log(' FavoriteRoutes: Order clicked for route:', route); // DODAJ
    console.log(' FavoriteRoutes: Setting prefilled data:', {
    origin: route.origin.address,
    destination: route.destination.address
    });

    // Send origin and destination to ride ordering form
    this.sharedRideDataService.setPrefilledData({
      origin: route.origin.address,
      destination: route.destination.address
    });

    console.log(' FavoriteRoutes: Navigating to main page'); // DODAJ
    // Navigate to main page
    this.router.navigate(['/']);
  }
}

