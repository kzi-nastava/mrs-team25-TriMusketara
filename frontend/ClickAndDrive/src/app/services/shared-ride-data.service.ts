// Service for sharing data between components
import { Injectable } from "@angular/core";
import { BehaviorSubject} from "rxjs";

export interface PrefilledRideData {
    origin: string;
    destination: string;
}

@Injectable({providedIn: 'root'})
export class SharedRideDataService {
    private prefilledDataSubject = new BehaviorSubject<PrefilledRideData | null>(null);
    public prefilledData$ = this.prefilledDataSubject.asObservable();

    private storedData: PrefilledRideData | null = null;

    setPrefilledData(data: PrefilledRideData) {
        this.storedData = data;
        this.prefilledDataSubject.next(data);
        console.log(' Service: Stored prefilled data:', this.storedData);
    }

    getStoredData(): PrefilledRideData | null {
        console.log(' Service: Getting stored data:', this.storedData);
        return this.storedData;
    }

    clearPrefilledData() {
        console.log(' Service: CLEARING prefilled data (was:', this.storedData, ')');
        this.storedData = null;
        this.prefilledDataSubject.next(null);
    }
}