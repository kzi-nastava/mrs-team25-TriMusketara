import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-block-reason-alert',
  imports: [CommonModule, FormsModule],
  templateUrl: './block-reason-alert.html',
  styleUrl: './block-reason-alert.css',
})
export class BlockReasonAlert {
  isOpen = false;
  reason = '';

  open(reason: string) {
    this.reason = reason;
    this.isOpen = true;
  }

  close() {
    this.isOpen = false;
  }
}
