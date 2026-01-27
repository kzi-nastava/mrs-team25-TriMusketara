import { Location } from "./location";
export interface DriverRideHistory{
    id: number;
    startTime: string; 
    endTime: string;
    origin: Location;
    destination: Location;
    totalPrice: number;
    passengerEmails: string[];
    panicPressed: boolean;
}