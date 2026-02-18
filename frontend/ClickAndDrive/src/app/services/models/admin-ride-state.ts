export interface AdminRideStateDTO {
  rideId: number;
  driverEmail: string;
  passengerEmails: string[];
  currentLocation: Location;
  startTime: string;
  status: string;
  originAddress?: string;
  destinationAddress?: string;
}
