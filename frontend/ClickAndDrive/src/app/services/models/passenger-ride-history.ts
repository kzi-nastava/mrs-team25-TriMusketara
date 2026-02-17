export interface PassengerRideHistory {
  id: number;
  startTime: string;
  endTime: string;
  origin: {
    longitude: number;
    latitude: number;
    address: string;
  };
  destination: {
    longitude: number;
    latitude: number;
    address: string;
  };
  totalPrice: number;
  driverEmail: string;
  status: string;
}
