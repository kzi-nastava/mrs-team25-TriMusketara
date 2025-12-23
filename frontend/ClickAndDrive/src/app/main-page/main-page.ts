import { Component } from '@angular/core';
import { MapViewComponent } from '../components/map-view/map-view';
<<<<<<< Updated upstream
=======
import { RouterOutlet } from '@angular/router';
>>>>>>> Stashed changes

@Component({
  selector: 'app-main-page',
  standalone: true,
<<<<<<< Updated upstream
  imports: [MapViewComponent], 
=======
  imports: [MapViewComponent, RouterOutlet],
>>>>>>> Stashed changes
  templateUrl: './main-page.html',
  styleUrl: './main-page.css'
})
export class MainPageComponent {}