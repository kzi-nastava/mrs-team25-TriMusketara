package com.example.clickanddrive.dtosample.requests;

import com.example.clickanddrive.dtosample.LocationDTO;
import java.io.Serializable;

public class CreateGuestRideRequest implements Serializable {
    private LocationDTO origin;
    private LocationDTO destination;

    public CreateGuestRideRequest(LocationDTO origin, LocationDTO destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public LocationDTO getOrigin() { return origin; }
    public void setOrigin(LocationDTO origin) { this.origin = origin; }

    public LocationDTO getDestination() { return destination; }
    public void setDestination(LocationDTO destination) { this.destination = destination; }
}
