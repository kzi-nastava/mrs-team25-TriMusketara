package com.example.demo.services.interfaces;

import com.example.demo.dto.request.CreateRideRequestDTO;
import com.example.demo.dto.response.RideResponseDTO;

public interface RideService {
    RideResponseDTO createRide(CreateRideRequestDTO request);
}
