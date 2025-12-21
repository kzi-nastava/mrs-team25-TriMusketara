import { Component } from '@angular/core';
import { MapViewComponent } from '../components/map-view/map-view';

@Component({
  selector: 'app-main-page',
  standalone: true,
  imports: [MapViewComponent], 
  templateUrl: './main-page.html',
  styleUrl: './main-page.css'
})
export class MainPageComponent {}