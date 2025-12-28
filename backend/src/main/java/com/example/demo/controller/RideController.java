package com.example.demo.controller;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.request.RideFromFavoritesRequestDTO;
import com.example.demo.dto.request.RideRequestDTO;
import com.example.demo.dto.response.RideResponseDTO;
import com.example.demo.model.RideStatus;
import com.example.demo.dto.request.RideCancellationRequestDTO;
import com.example.demo.dto.request.RideRequestUnregisteredDTO;
import com.example.demo.dto.request.RideStopRequestDTO;
import com.example.demo.dto.response.RideEstimateResponseDTO;
import com.example.demo.dto.response.RideTrackingResponseDTO;
import com.example.demo.dto.request.InconsistencyReportRequestDTO;
import com.example.demo.dto.request.ReviewRequestDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @PostMapping
    public ResponseEntity<RideResponseDTO> createRide(
            @RequestBody RideRequestDTO request) {

        boolean hasAvailableDriver = true;
        double price = 500.0;

        // No drivers available
        if(!hasAvailableDriver){
            RideResponseDTO response = new RideResponseDTO(
                    null,
                    RideStatus.FAILED,
                    0
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Some driver is available
        RideResponseDTO response = new RideResponseDTO(
                1L,
                RideStatus.CREATED,
                price
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Ordering ride from favorites
    @PostMapping("/favorites")
    public ResponseEntity<RideResponseDTO> createRideFromFavorites(
            @RequestBody RideFromFavoritesRequestDTO request) {
        RideResponseDTO response = new RideResponseDTO(
                2L,
                RideStatus.CREATED,
                600.0
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Starting the ride
    @PutMapping("/{rideId}/start")
    public ResponseEntity<RideResponseDTO> startRide(@PathVariable Long rideId) {
        RideResponseDTO response = new RideResponseDTO(
                rideId,
                RideStatus.STARTED,
                0
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ping")
    public String ping() {
            return "RideController radi!";
    }

    @PostMapping("/estimate")
    public ResponseEntity<RideEstimateResponseDTO> estimateRide(
            @RequestBody RideRequestUnregisteredDTO request
    ) {
        RideEstimateResponseDTO response = new RideEstimateResponseDTO();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelRide(
            @PathVariable Long id,
            @RequestBody RideCancellationRequestDTO request
    ) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<Void> stopRide(
            @PathVariable Long id,
            @RequestBody RideStopRequestDTO request
    ) {
        return ResponseEntity.ok().build();
    }

    // 2.6.2: Following the ride
    @GetMapping("/{id}/tracking")
    public ResponseEntity<RideTrackingResponseDTO> getRideTracking(@PathVariable Long id) {
        return ResponseEntity.ok(new RideTrackingResponseDTO(id, new LocationDTO( 45.26, 19.83, "Bul. Oslobodjenja"), 5));
    }

    // TAČKA 2.6.2: inconsistency report
    @PostMapping("/{id}/inconsistency")
    public ResponseEntity<Void> reportInconsistency(@PathVariable Long id,
                                                    @RequestBody InconsistencyReportRequestDTO request) {
        return ResponseEntity.ok().build();
    }

    // TAČKA 2.7: Finish ride
    @PutMapping("/{id}/finish")
    public ResponseEntity<Void> finishRide(@PathVariable Long id) {
        return ResponseEntity.ok().build();
    }

    // 2.8: Rating
    @PostMapping("/{id}/review")
    public ResponseEntity<Void> reviewRide(@PathVariable Long id, @RequestBody ReviewRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
