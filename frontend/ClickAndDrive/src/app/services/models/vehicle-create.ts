// This interface represents info for driver vehicle registration
export interface VehicleCreate {
    model: string;
    type: string;
    registration: string;
    seats: number;
    babyFriendly: boolean;
    petFriendly: boolean;
}