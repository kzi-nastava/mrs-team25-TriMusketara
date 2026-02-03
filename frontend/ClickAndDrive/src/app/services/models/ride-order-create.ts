import { Location } from "./location";

// Interface for when a registered user orders a ride, the object is created and sent to backend
export interface RideOrderCreate {
    passengerId: number; // which passenger created the ride
    origin: Location;
    destination: Location;
    stops: Location[];
    passengerEmails: string[];
    vehicleType: 'STANDARD' | 'LUXURY' | 'VAN'
    scheduledTime: string;
    babyFriendly: boolean;
    petFriendly: boolean;
    durationMinutes: number;
    distanceKm: number;
}