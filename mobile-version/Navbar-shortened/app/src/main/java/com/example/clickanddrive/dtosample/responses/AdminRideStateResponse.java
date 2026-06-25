package com.example.clickanddrive.dtosample.responses;

import com.example.clickanddrive.dtosample.LocationDTO;
import java.util.List;

public class AdminRideStateResponse {
    private Long rideId;
    private String driverEmail;
    private List<String> passengerEmails;
    private LocationDTO currentLocation;
    private String originAddress;
    private String destinationAddress;
    private String startTime;
    private String status;

    public Long getRideId() { return rideId; }
    public String getDriverEmail() { return driverEmail; }
    public List<String> getPassengerEmails() { return passengerEmails; }
    public LocationDTO getCurrentLocation() { return currentLocation; }
    public String getOriginAddress() { return originAddress; }
    public String getDestinationAddress() { return destinationAddress; }
    public String getStartTime() { return startTime; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        String driver = driverEmail == null || driverEmail.trim().isEmpty() ? "Unknown driver" : driverEmail;
        return "#" + rideId + " - " + driver;
    }
}