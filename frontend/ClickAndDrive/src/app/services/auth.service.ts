import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import {Map} from './map'

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

    currentRouteCoordinates = signal<[number, number][]>([]);

    isBlocked = signal(false);
    blockReason = signal('');

    private apiUrl = 'http://localhost:8080/api/user';

    constructor(private http: HttpClient, private router: Router) {
        this.loadBlockStatus();
    }

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

    getUserId(): number{
        const token = localStorage.getItem('token');
        if(token){
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.userId;
        }
        return 0;
    }

    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('isBlocked');
        localStorage.removeItem('blockReason');
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

    // ===================== REGISTRATION + ACTIVATION =====================

    registerPassenger(registerData: any) {
        return this.http.post(`${this.apiUrl}/auth/register`, registerData);
    }

    activateAccount(token: string) {
        return this.http.get(`${this.apiUrl}/auth/activate/${token}`);
    }

    // User blocking helpers
    setBlockStatus(blocked: boolean, reason: string | null) {
        this.isBlocked.set(!!blocked);
        this.blockReason.set(reason || '');

        // Save users state to localSotrage
        localStorage.setItem('isBlocked', String(!!blocked));
        if (reason) localStorage.setItem('blockReason', reason);
    }

    loadBlockStatus() {
        const blocked = localStorage.getItem('isBlocked') === 'true';
        const reason = localStorage.getItem('blockReason') || '';
        this.isBlocked.set(blocked);
        this.blockReason.set(reason);
    }

    isUserBlocked(): boolean {
        return this.isBlocked();
    }

    getBlockedReason(): string {
        return this.blockReason();
    }

}
