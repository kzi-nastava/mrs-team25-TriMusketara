import {Injectable, signal} from '@angular/core';

@Injectable({
    providedIn: 'root'
})

export class AuthService {
    // Signal for user type
    userType = signal<'guest' | 'user' | 'driver' | 'admin'>('user');

    // Username
    userName = signal('TriMusketara');

    // In-drive status
    inDrive = signal(true);

    // Signals to store the current ride data
    origin = signal('');
    destination = signal('');
    eta = signal(0); 

    // Set which user
    setUserType(type: 'guest' | 'user' | 'driver' | 'admin') {
        this.userType.set(type);
    }

    // Set username
    setUsername(name: string) {
        this.userName.set(name);
    }

    setInDrive(status: boolean) {
        this.inDrive.set(status);
    }

    // Method to update current ride details
    setRideData(origin: string, destination: string) {
        this.origin.set(origin);
        this.destination.set(destination);
    }
}
