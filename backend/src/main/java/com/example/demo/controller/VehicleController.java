package com.example.demo.controller;

import com.example.demo.dto.response.ActiveVehicleResponseDTO;
import com.example.demo.services.interfaces.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "http://localhost:4200")
public class VehicleController {

    private final VehicleService vehicleService;


    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // 2.1.1: Start view, all vehicles
    @GetMapping("/active")
    public ResponseEntity<List<ActiveVehicleResponseDTO>> getActiveVehicles() {
        List<ActiveVehicleResponseDTO> activeVehicles = vehicleService.getAllActiveVehicles();
        return ResponseEntity.ok(activeVehicles);
    }
}