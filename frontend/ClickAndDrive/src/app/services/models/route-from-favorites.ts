import { Location } from "./location";

// Interface for mapping route info from backend
export interface RouteFromFavorites {
    id: number;
    origin: Location;
    destination: Location;
    distance: number;
    duration: number;
    timesUsed: number;
    favorite: boolean;
}
