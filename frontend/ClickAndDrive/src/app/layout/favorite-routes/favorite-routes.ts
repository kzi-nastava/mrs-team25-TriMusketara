import { ChangeDetectorRef, Component } from '@angular/core';
import { FavoriteToggle } from '../../components/favorite-toggle/favorite-toggle';
import { RouteFromFavorites } from '../../services/models/route-from-favorites';
import { PassengerService } from '../../services/passenger.service';
import { AuthService } from '../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';

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

  constructor(private passengerService: PassengerService, private cdr: ChangeDetectorRef, private authService: AuthService, private toastr: ToastrService, private router: Router) {}

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

  onFavoriteToggle(route: RouteDTO, active:boolean) {
    route.favorite = active;
  }
}
