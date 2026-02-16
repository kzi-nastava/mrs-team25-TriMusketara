import { Component, computed } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-driver-notes-page',
  imports: [],
  templateUrl: './driver-notes-page.html',
  styleUrl: './driver-notes-page.css',
})
export class DriverNotesPage {


  constructor(private authService: AuthService) {}

  // Computed = immediate update after change of signal
  isBlocked = computed(() => this.authService.isUserBlocked());

  blockReason = computed(() => {
    const r = this.authService.getBlockedReason();
    return r?.trim() ? r : 'No reson provided.';
  });
}
