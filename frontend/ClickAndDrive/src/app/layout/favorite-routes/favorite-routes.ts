import { ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { FavoriteToggle } from '../../components/favorite-toggle/favorite-toggle';
import { RouteFromFavorites } from '../../services/models/route-from-favorites';
import { PassengerService } from '../../services/passenger.service';
import { AuthService } from '../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { SharedRideDataService } from '../../services/shared-ride-data.service';
import { BlockReasonAlert } from '../../components/block-reason-alert/block-reason-alert';

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
  imports: [FavoriteToggle, BlockReasonAlert],
  templateUrl: './favorite-routes.html',
  styleUrl: './favorite-routes.css',
})
export class FavoriteRoutes {

  favoriteRoutes: RouteFromFavorites[] = [];
  
  private _blockedAlert: BlockReasonAlert | undefined;

  @ViewChild(BlockReasonAlert)
  set blockedAlert(value: BlockReasonAlert) {
    this._blockedAlert = value;
  }

  get blockedAlert(): BlockReasonAlert {
    return this._blockedAlert!;
  }

  constructor(private passengerService: PassengerService, private cdr: ChangeDetectorRef, private authService: AuthService, private toastr: ToastrService, private router: Router, private sharedRideDataService: SharedRideDataService) {}

  routes: RouteDTO[] = [
    { id: 1, from: 'Beograd', to: 'Novi Sad', favorite: true, distance: 96, duration: 70, timesUsed: 4},
    { id: 2, from: 'Nis', to: 'Beograd', favorite: true, distance: 235, duration: 130, timesUsed: 3 },
    { id: 3, from: 'Subotica', to: 'Novi Sad', favorite: true, distance: 106, duration: 85, timesUsed: 2},
  ];

  userId!: number;

  currentPage = 0;
  pageSize = 3;
  totalPages = 0;

  ngOnInit(): void {
    const userIdFromToken = this.authService.getUserIdFromToken();
    if (userIdFromToken) {
      this.userId = userIdFromToken;
    } else {
      this.toastr.error('User not authenticated', 'Error');
      this.router.navigate(['/login']);
      return;
    }

    this.loadRoutes();
  }

  // Click on the heart button (remove from favorites)
  onFavoriteToggle(route: RouteFromFavorites, active:boolean) {
    route.favorite = active;
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

  loadRoutes(): void {
    this.passengerService.getFavoriteRoutes(this.userId, this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.favoriteRoutes = response.content.map((r: any) => ({ ...r, favorite: true }));
        this.totalPages = response.totalPages;
        this.cdr.detectChanges();
      },
      error: (err) => { this.toastr.error(err, 'Error'); }
    });
  }

  // Order favorite route
  onOrderClick(route: RouteFromFavorites) {

    // Check users status - if blocked he can not order new rides
    if (this.authService.isUserBlocked()) {
      const reason = this.authService.getBlockedReason();
      this.blockedAlert.open(reason || 'No specific reason provided');
      return;
    } 

    // Send origin and destination to ride ordering form
    this.sharedRideDataService.setPrefilledData({
      origin: route.origin.address,
      destination: route.destination.address
    });

    // Navigate to main page
    this.router.navigate(['/']);
  }

  // Navigating through pages
  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadRoutes();
    }
  }
  
  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadRoutes();
    }
  }
}
