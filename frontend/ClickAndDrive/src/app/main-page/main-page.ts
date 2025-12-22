import { Component, Input } from '@angular/core'; // 1. Ensure Input is imported
import { CommonModule } from '@angular/common';
import { MapViewComponent } from '../components/map-view/map-view';
import { DriverHistory } from '../layout/driver-history/driver-history';

@Component({
  selector: 'app-main-page',
  standalone: true,
  imports: [CommonModule, MapViewComponent, DriverHistory],
  templateUrl: './main-page.html',
  styleUrl: './main-page.css'
})
export class MainPageComponent {
  // 2. Add the @Input() decorator here
  // This allows the App component to bind to [currentView]
  @Input() currentView: string = 'map'; 
}