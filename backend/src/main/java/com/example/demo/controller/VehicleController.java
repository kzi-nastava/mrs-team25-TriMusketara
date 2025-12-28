package com.example.demo.controller;

import com.example.demo.dto.response.ActiveVehicleResponseDTO;
import com.example.demo.dto.LocationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    // 2.1.1: Start view, all vehicles
    @GetMapping("/active")
    public ResponseEntity<List<ActiveVehicleResponseDTO>> getActiveVehicles() {
        return ResponseEntity.ok(List.of(
                new ActiveVehicleResponseDTO(1L, new LocationDTO( 45.2, 19.8, "Adresa 1"), false),
                new ActiveVehicleResponseDTO(2L, new LocationDTO( 45.3, 19.9, "Adresa 2"), true)
        ));
    }
}