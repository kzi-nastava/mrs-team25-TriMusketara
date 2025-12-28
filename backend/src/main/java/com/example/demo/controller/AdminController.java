package com.example.demo.controller;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.request.DriverRegistrationRequestDTO;
import com.example.demo.dto.response.DriverRegistrationResponseDTO;
import com.example.demo.model.DriverStatus;
import com.example.demo.dto.response.RideDetailsResponseDTO;
import com.example.demo.dto.response.RideHistoryResponseDTO;
import com.example.demo.dto.response.AdminRideStateResponseDTO;
import com.example.demo.dto.VehiclePriceDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PostMapping("/drivers")
    public ResponseEntity<DriverRegistrationResponseDTO> registerDriver(
            @RequestBody DriverRegistrationRequestDTO request) {

        DriverRegistrationResponseDTO response = new DriverRegistrationResponseDTO(
                1L,
                request.getEmail(),
                request.getName(),
                request.getSurname(),
                DriverStatus.ACTIVE
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/rides")
    public ResponseEntity<List<RideHistoryResponseDTO>> getRideHistory(
            @RequestParam Long userId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/rides/{id}")
    public ResponseEntity<RideDetailsResponseDTO> getRideDetails(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(new RideDetailsResponseDTO());
    }

    // 2.13: State of ride of any driver
    @GetMapping("/rides/active/{driverId}")
    public ResponseEntity<AdminRideStateResponseDTO> getActiveRideByDriver(@PathVariable Long driverId) {
        AdminRideStateResponseDTO response = new AdminRideStateResponseDTO(
                101L, "driver@uber.com", List.of("pass1@gmail.com"),
                new LocationDTO( 45.25, 19.84, "Trg Slobode"),
                LocalDateTime.now(), "STARTED"
        );
        return ResponseEntity.ok(response);
    }

    // 2.14: GET prices
    @GetMapping("/prices")
    public ResponseEntity<VehiclePriceDTO> getVehiclePrices() {
        return ResponseEntity.ok(new VehiclePriceDTO(200.0, 500.0, 300.0, 120.0));
    }

    // 2.14: UPDATE prices
    @PutMapping("/prices")
    public ResponseEntity<VehiclePriceDTO> updateVehiclePrices(@RequestBody VehiclePriceDTO request) {
        return ResponseEntity.ok(new VehiclePriceDTO(
                request.getStandardBasePrice(), request.getLuxuryBasePrice(),
                request.getVanBasePrice(), request.getPricePerKm()));
    }

}
