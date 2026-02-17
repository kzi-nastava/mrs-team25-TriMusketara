import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AdminPopupService {
  private isOpenSignal = signal(false);

  isOpen() { return this.isOpenSignal(); }

  open() { this.isOpenSignal.set(true); }

  close() { this.isOpenSignal.set(false); }
}