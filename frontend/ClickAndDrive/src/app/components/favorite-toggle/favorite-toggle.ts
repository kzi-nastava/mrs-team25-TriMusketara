import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-favorite-toggle',
  imports: [],
  templateUrl: './favorite-toggle.html',
  styleUrl: './favorite-toggle.css',
})
export class FavoriteToggle {
  @Input() active = false;
  @Output() toggled = new EventEmitter<boolean>();

  toggle(event: MouseEvent) {
    event.stopPropagation();
    this.active = !this.active;
    this.toggled.emit(this.active);
  }
}
