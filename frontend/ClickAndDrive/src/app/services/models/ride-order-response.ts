// This interface is used for mapping a RESPONSE when ordering a ride from backend
// Can be expanded later

export interface RideOrderResponse {
    id: number;
    status: 'CREATED' | 'SCHEDULED' | 'STARTED' | 'FINISHED' | 'CANCELED' | 'FAILED';
    price: number;
}