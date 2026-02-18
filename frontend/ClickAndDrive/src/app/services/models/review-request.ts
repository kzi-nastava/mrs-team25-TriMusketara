export interface ReviewRequest {
  rideId: number;
  passengerId: number;
  driverRating: number;
  vehicleRating: number;
  comment: string;
}