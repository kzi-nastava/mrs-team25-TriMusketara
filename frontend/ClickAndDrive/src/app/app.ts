import { Component, signal } from '@angular/core';
import { NavbarComponent } from './components/navbar/navbar';
import { MainPageComponent } from './main-page/main-page';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NavbarComponent, MainPageComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('ClickAndDrive');

  // State shared between siblings
  activeView: string = 'map';

  // Update state when Navbar sends an event
  onViewChange(newView: string) {
    this.activeView = newView;
  }
}