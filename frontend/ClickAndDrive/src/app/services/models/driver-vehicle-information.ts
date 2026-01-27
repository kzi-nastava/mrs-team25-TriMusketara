// This interface is used for mapping info from backend when retreiving 
// driver vehicle information for profile/change-info display
// This will also be used when sending back changed information from frontend to backend
export interface VehicleInformation {
    id?: number;
    model: string;
    type: 'STANDARD' | 'LUXURY' | 'VAN';
    registration: string;
    isBabyFriendly: boolean;
    isPetFriendly: boolean;
}