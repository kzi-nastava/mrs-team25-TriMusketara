import { Component, OnInit, inject, ChangeDetectorRef  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PanicService, PanicResponse } from '../../services/panic.service';
import '@angular/compiler';

@Component({
  selector: 'app-admin-panic-notifications',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './panic-notifications.html',
  styleUrl: './panic-notifications.css',
})
export class PanicNotifications implements OnInit {
  private panicService = inject(PanicService);
  private cdr = inject(ChangeDetectorRef);

  panics: PanicResponse[] = [];
  loading = false;
  audio = new Audio(); // Za zvučnu notifikaciju

  ngOnInit() {
    this.loadPanics();
    this.playSound(); // Pusti zvuk kada se učita komponenta
  }

  loadPanics() {
    this.loading = true;
    this.panicService.getUnresolvedPanics().subscribe({
      next: (data) => {
        console.log('PANICS RECEIVED:', data);
        this.panics = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load panic notifications:', err);
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  resolvePanic(panicId: number) {
    if (!confirm('Mark this panic as resolved?')) return;

    this.panicService.resolvePanic(panicId).subscribe({
      next: () => {
        this.panics = this.panics.filter(p => p.id !== panicId);
        alert('Panic resolved successfully');
      },
      error: (err) => {
        console.error('Failed to resolve panic:', err);
        alert('Failed to resolve panic');
      }
    });
  }

  playSound() {
    // Koristimo jednostavan beep sound preko Web Audio API
    const audioContext = new (window.AudioContext || (window as any).webkitAudioContext)();
    const oscillator = audioContext.createOscillator();
    const gainNode = audioContext.createGain();

    oscillator.connect(gainNode);
    gainNode.connect(audioContext.destination);

    oscillator.frequency.value = 800; // Frekvencija zvuka
    oscillator.type = 'sine';

    gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
    gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5);

    oscillator.start(audioContext.currentTime);
    oscillator.stop(audioContext.currentTime + 0.5);
  }
}