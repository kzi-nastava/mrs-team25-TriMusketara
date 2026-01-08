import { Component } from '@angular/core';
import { MapViewComponent } from '../../components/map-view/map-view';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-drive-in-progress',
  imports: [RouterOutlet, MapViewComponent],
  templateUrl: './drive-in-progress.html',
  styleUrl: './drive-in-progress.css',
})
export class DriveInProgress {

}
