export interface AdminRideHistory {
  id: number;
  startTime: string;
  endTime: string;
  origin: any;
  destination: any;
  totalPrice: number;
  panicPressed: boolean;
  cancelled: boolean;
  cancelledBy: string;
  driverEmail: string;
  passengerEmails: string[];
  status: string;
}
