import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface PanicRequest {
  rideId: number;
  guest: boolean;
  userId: number;
}

export interface PanicResponse {
  id: number;
  rideId: number;
  isGuest: boolean;
  triggeredByName: string;
  triggeredByEmail: string;
  createdAt: string;
  resolved: boolean;
  originAddress: string;
  destinationAddress: string;
}

@Injectable({ providedIn: 'root' })
export class PanicService {
  private apiUrl = 'http://localhost:8080/api/panic';

  constructor(private http: HttpClient) {}

  triggerPanic(request: PanicRequest): Observable<PanicResponse> {
    console.log('PanicService - sending request:', request);
    return this.http.post<PanicResponse>(this.apiUrl, request);
  }

  getAllPanics(): Observable<PanicResponse[]> {
    return this.http.get<PanicResponse[]>(`${this.apiUrl}/all`);
  }

  getUnresolvedPanics(): Observable<PanicResponse[]> {
    return this.http.get<PanicResponse[]>(`${this.apiUrl}/unresolved`);
  }

  resolvePanic(panicId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${panicId}/resolve`, {});
  }
}