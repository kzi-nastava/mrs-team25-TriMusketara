import { Injectable, signal } from "@angular/core";

// Simple tracking if the profile sidebar is opened or not
@Injectable({providedIn: 'root'})
export class ProfileSidebarService {
    isOpen = signal(false);

    open() {
        this.isOpen.set(true);
    }

    close() {
        this.isOpen.set(false);
    }

    toggle() {
        this.isOpen.update(v => !v);
    }
}
