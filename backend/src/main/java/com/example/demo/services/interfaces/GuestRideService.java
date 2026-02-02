package com.example.demo.services.interfaces;

import com.example.demo.dto.request.RideRequestUnregisteredDTO;
import com.example.demo.dto.response.GuestRideResponseDTO;

public interface GuestRideService {
    GuestRideResponseDTO createGuestRide(RideRequestUnregisteredDTO request);
    void cancelGuestRide(Long rideId);
}
