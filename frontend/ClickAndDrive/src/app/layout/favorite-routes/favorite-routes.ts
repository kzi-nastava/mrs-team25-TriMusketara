import { Component } from '@angular/core';
import { FavoriteToggle } from '../../components/favorite-toggle/favorite-toggle';

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

  routes: RouteDTO[] = [
    { id: 1, from: 'Beograd', to: 'Novi Sad', favorite: true, distance: 96, duration: 70, timesUsed: 4},
    { id: 2, from: 'Nis', to: 'Beograd', favorite: true, distance: 235, duration: 130, timesUsed: 3 },
    { id: 3, from: 'Subotica', to: 'Novi Sad', favorite: true, distance: 106, duration: 85, timesUsed: 2},
  ];

  onFavoriteToggle(route: RouteDTO, active:boolean) {
    route.favorite = active;
  }
}
