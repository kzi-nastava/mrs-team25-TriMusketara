package com.example.demo.services.interfaces;

import com.example.demo.dto.request.CompleteRegistrationRequestDTO;
import com.example.demo.dto.request.DriverRegistrationRequestDTO;
import com.example.demo.dto.response.DriverRegistrationResponseDTO;
import com.example.demo.dto.response.VehicleResponseDTO;

public interface DriverService {
    DriverRegistrationResponseDTO registerDriver(DriverRegistrationRequestDTO request);
    void completeRegistration(CompleteRegistrationRequestDTO request);
    boolean isTokenValid(String token);
    VehicleResponseDTO getDriverVehicle(Long id);
}
