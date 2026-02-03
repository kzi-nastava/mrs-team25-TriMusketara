import { Location } from './location';

export interface ActiveVehicleResponse {
  id: number;
  currentLocation: Location;
  busy: boolean;
}