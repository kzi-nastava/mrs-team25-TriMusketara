import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ActiveVehicleResponse } from './models/active-vehicle-response';

@Injectable({ providedIn: 'root' })
export class VehicleService {
  private apiUrl = 'http://localhost:8080/api/vehicles';

  constructor(private http: HttpClient) {}

  getActiveVehicles(): Observable<ActiveVehicleResponse[]> {

    var response = this.http.get<ActiveVehicleResponse[]>(`${this.apiUrl}/active`);
    //console.log("Got active vehicles:", response);
    return response;
  }
}