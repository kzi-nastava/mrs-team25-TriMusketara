import { Location } from "./location";

// Interface for when a registered user orders a ride
export interface RideOrderCreate {
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