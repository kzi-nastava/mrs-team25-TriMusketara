import { VehicleCreate } from "./vehicle-create";

// This interface represents info for driver registration
export interface DriverCreate {
    name: string;
    surname: string;
    email: string;
    gender: 'MALE' | 'FEMALE';
    address: string;
    phone: string;
    vehicle: VehicleCreate;
}