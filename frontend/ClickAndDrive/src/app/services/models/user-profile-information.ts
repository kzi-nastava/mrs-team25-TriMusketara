// This interface is used for mapping info from backend when retreiving 
// users information for profile/change-info display

import { VehicleInformation } from "./driver-vehicle-information"

// This will also be used when sending back changed information from frontend to backend
export interface UserProfileInformation {
    id: number,
    email: string,
    name: string,
    surname: string,
    address: string,
    phone: string
    // Only for drivers
    vehicle?: VehicleInformation
    profileImageUrl?: string;
}