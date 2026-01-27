package com.example.demo.services.interfaces;

import com.example.demo.dto.request.CreateRideRequestDTO;
import com.example.demo.dto.request.RideRequestUnregisteredDTO;
import com.example.demo.dto.response.RideEstimateResponseDTO;
import com.example.demo.dto.response.RideResponseDTO;

public interface RideService {
    RideResponseDTO createRide(CreateRideRequestDTO request);
    RideEstimateResponseDTO estimateRide(RideRequestUnregisteredDTO request);
}
