import { Component } from '@angular/core';
import { MapViewComponent } from '../components/map-view/map-view';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-main-page',
  standalone: true,
  imports: [MapViewComponent, RouterOutlet], 
  templateUrl: './main-page.html',
  styleUrl: './main-page.css'
})
export class MainPageComponent {}