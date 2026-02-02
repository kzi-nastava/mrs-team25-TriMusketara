export interface GuestRideResponseDTO {
  id: number;
  status: 'CREATED' | 'SCHEDULED' | 'FAILED' | 'STARTED';
  estimatedTimeMinutes: number;
  distanceKm: number;
}