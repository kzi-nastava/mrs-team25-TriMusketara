package com.example.clickanddrive.dtosample.requests;

import com.example.clickanddrive.dtosample.LocationDTO;

public class RideStopRequest {

    private Boolean guest;
    private LocationDTO stopLocation;

    public RideStopRequest(Boolean guest, LocationDTO stopLocation) {
        this.guest = guest;
        this.stopLocation = stopLocation;
    }

    public Boolean getGuest() {
        return guest;
    }

    public void setGuest(Boolean guest) {
        this.guest = guest;
    }

    public LocationDTO getStopLocation() {
        return stopLocation;
    }

    public void setStopLocation(LocationDTO stopLocation) {
        this.stopLocation = stopLocation;
    }
}