import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { WebSocketService } from '../../services/web-socket.service';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-passenger-notes-page',
  imports: [CommonModule],
  templateUrl: './passenger-notes-page.html',
  styleUrl: './passenger-notes-page.css',
})
export class PassengerNotesPage implements OnInit {

  notes: any[] = []; // here we save messages

  constructor(private webSocketService: WebSocketService, private authService: AuthService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.webSocketService.unreadCount$.next(0);

    this.webSocketService.notificationReceived$.subscribe((note) => {
      this.notes.unshift(note);
      this.cdr.detectChanges();
    });
  }

  clearNotes() {
    this.notes = [];
    this.cdr.detectChanges();
  }
}
