import {Injectable, signal} from '@angular/core';

@Injectable({
    providedIn: 'root'
})

export class AuthService {
    // Signal for user type
    userType = signal<'guest' | 'user' | 'driver' | 'admin'>('user');

    // Username
    userName = signal('TriMusketara');

    //
    inDrive = signal(false);

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
}
