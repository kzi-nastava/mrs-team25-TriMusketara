package com.example.demo.services.interfaces;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.InconsistencyReportResponseDTO;
import com.example.demo.dto.response.RideEstimateResponseDTO;
import com.example.demo.dto.response.RideResponseDTO;
import jakarta.validation.Valid;

public interface RideService {
    RideResponseDTO createRide(CreateRideRequestDTO request);
    RideEstimateResponseDTO estimateRide(RideRequestUnregisteredDTO request);
    void cancelRide(Long rideId, RideCancellationRequestDTO request);
    void panic(Long rideId);
    void stopRide(Long rideId, RideStopRequestDTO request);
    void finishRide(Long rideId, String driverEmail);

    InconsistencyReportResponseDTO reportInconsistency(Long id,
                                                       @Valid InconsistencyReportRequestDTO dto,
                                                       String name);

}
