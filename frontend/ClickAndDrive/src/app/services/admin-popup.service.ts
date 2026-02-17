import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AdminPopupService {
  private isPriceOpenSignal = signal(false);
  private isChatOpenSignal = signal(false);

  isPriceOpen() { return this.isPriceOpenSignal(); }
  isChatOpen() { return this.isChatOpenSignal(); }

  openPrice() { this.isPriceOpenSignal.set(true); }
  closePrice() { this.isPriceOpenSignal.set(false); }

  openChat() { this.isChatOpenSignal.set(true); }
  closeChat() { this.isChatOpenSignal.set(false); }
}