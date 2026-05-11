package com.example.clickanddrive.dtosample.responses;

import com.example.clickanddrive.dtosample.LocationDTO;

public class ActiveVehicleResponse {
    private Long id;
    private LocationDTO currentLocation;
    private boolean busy;

    public ActiveVehicleResponse() {
    }

    public ActiveVehicleResponse(Long id, LocationDTO currentLocation, boolean busy) {
        this.id = id;
        this.currentLocation = currentLocation;
        this.busy = busy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocationDTO getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationDTO currentLocation) {
        this.currentLocation = currentLocation;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}