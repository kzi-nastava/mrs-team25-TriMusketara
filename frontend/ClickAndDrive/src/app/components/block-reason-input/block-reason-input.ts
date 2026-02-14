import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-block-reason-input',
  imports: [CommonModule, FormsModule],
  templateUrl: './block-reason-input.html',
  styleUrl: './block-reason-input.css',
})
export class BlockReasonInput {

  @Input() title: string = 'Leave a note';
  @Input() initialValue: string = '';
  @Output() onConfirm = new EventEmitter<string>();
  @Output() onClose = new EventEmitter<void>();

  isOpen = false;
  reason = '';

  open(currentReason: string = '') {
    this.reason = currentReason;
    this.isOpen = true;
  }

  close() {
    this.isOpen = false;
    this.onClose.emit();
  }

  confirm() {
    this.onConfirm.emit(this.reason);
    this.close();
  }
}
