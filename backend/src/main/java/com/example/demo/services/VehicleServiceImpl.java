package com.example.demo.services;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.response.ActiveVehicleResponseDTO;
import com.example.demo.model.Vehicle;
import com.example.demo.repositories.VehicleRepository;
import com.example.demo.services.interfaces.VehicleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<ActiveVehicleResponseDTO> getAllActiveVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        return vehicles.stream().map(v -> new ActiveVehicleResponseDTO(
                v.getId(),
                new LocationDTO(
                        v.getLocation().getLatitude(),
                        v.getLocation().getLongitude(),
                        v.getLocation().getAddress()
                ),
                v.getBusy()
        )).collect(Collectors.toList());
    }
}