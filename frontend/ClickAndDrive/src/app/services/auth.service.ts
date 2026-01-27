import {Injectable, signal} from '@angular/core';

@Injectable({
    providedIn: 'root'
})

export class AuthService {
    // Signal for user type
    userType = signal<'guest' | 'user' | 'driver' | 'admin'>('guest');

    // Username
    userName = signal('TriMusketara');

    // In-drive status
    inDrive = signal(false);

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

    // Get users role from token
    getRoleFromToken(): string | null {
        const token = localStorage.getItem('token');
        if (!token) return null;
      
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          return payload.role || null;
        } catch {
          return null;
        }
      }
    
    // Get user ID from token
    // getUserIdFromToken(): number | null {
    //     const token = localStorage.getItem('token');
    //     if (!token) return null;
    
    //     try {
    //     const payload = JSON.parse(atob(token.split('.')[1]));
    //     return payload.userId || null;
    //     } catch {
    //     return null;
    //     }
    // }

    getUserIdFromToken(): number | null {
        const token = localStorage.getItem('token');
        if (!token) {
            console.log('No token found'); // Debug
            return null;
        }
      
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          console.log('Parsed payload:', payload); // Debug
          console.log('userId value:', payload.userId); // Debug
          return payload.userId || null;
        } catch (error) {
          console.error('Error parsing token:', error); // Debug
          return null;
        }
    }

    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.reload();
    }

    isLoggedIn(): boolean {
        return !!localStorage.getItem('token');
    }

    getIdFromToken(): number | null {
        const token = localStorage.getItem('token');
        if (!token) return null;

        try {
          const payload = JSON.parse(atob(token.split('.')[0]));
          return payload.id || null;
        } catch {
          return null;
        }
      }
}
