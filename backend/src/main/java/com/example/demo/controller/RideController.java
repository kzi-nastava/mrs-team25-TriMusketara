package com.example.demo.controller;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;
import com.example.demo.model.GuestRide;
import com.example.demo.model.Ride;
import com.example.demo.model.RideStatus;

import com.example.demo.model.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.interfaces.GuestRideService;
import com.example.demo.services.interfaces.ReviewService;
import com.example.demo.services.interfaces.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rides")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class RideController {

    // Service
    private final RideService rideService;
    private final ReviewService reviewService;
    private final GuestRideService guestRideService;
    private final UserRepository userRepository;

    @PostMapping("/create-ride")
    public ResponseEntity<RideResponseDTO> createRide(
            @Valid @RequestBody CreateRideRequestDTO request) {

        RideResponseDTO response = rideService.createRide(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Ordering ride from favorites
    @PostMapping("/favorites")
    public ResponseEntity<RouteFromFavoritesResponseDTO> createRideFromFavorites(
            @RequestBody RouteFromFavoritesRequestDTO request) {
        LocationDTO origin = new LocationDTO(
                45.2671,
                19.8335,
                "Novi Sad"
        );

        LocationDTO destination = new LocationDTO(
                44.7866,
                20.4489,
                "Beograd"
        );

        RouteFromFavoritesResponseDTO response =
                new RouteFromFavoritesResponseDTO(
                        1L,
                        origin,
                        destination,
                        100,
                        90,
                        5
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

    @PostMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelRide(
            @PathVariable Long id,
            @RequestBody RideCancellationRequestDTO request
    ) {
        rideService.cancelAnyRide(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<Void> stopRide(
            @PathVariable Long id,
            @Valid @RequestBody RideStopRequestDTO request
    ) {
        rideService.stopRide(id, request);
        return ResponseEntity.ok().build();
    }

    // 2.6.2: Following the ride
    @GetMapping("/{id}/tracking")
    public ResponseEntity<RideTrackingResponseDTO> getRideTracking(@PathVariable Long id) {
        return ResponseEntity.ok(new RideTrackingResponseDTO(id, new LocationDTO( 45.26, 19.83, "Bul. Oslobodjenja"), 5));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<Page<ScheduledRideResponseDTO>> getDriverScheduledRides(
            @PathVariable Long driverId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Page<ScheduledRideResponseDTO> rides = rideService.getDriverScheduledRides(driverId, page, size);
        return ResponseEntity.ok(rides);
    }

    // 2.6.2: inconsistency report
    @PostMapping("/{id}/inconsistency-report")
    @PreAuthorize("hasAuthority('Passenger')") // Samo putnici mogu da prijave
    public ResponseEntity<InconsistencyReportResponseDTO> reportInconsistency(
            @PathVariable Long id,
            @Valid @RequestBody InconsistencyReportRequestDTO dto,
            Principal principal) {

        return ResponseEntity.ok(rideService.reportInconsistency(id, dto, principal.getName()));
    }

    // 2.7: Finish ride
    @PutMapping("/{id}/finish")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Void> finishRide(@PathVariable Long id, Principal principal) {
        String driverEmail = principal.getName();

        rideService.finishRide(id, driverEmail);
        return ResponseEntity.ok().build();
    }

    // 2.8: Rating
    @PostMapping("/{id}/review")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRequestDTO dto) {
        // @Valid activates min, max, etc...
        reviewService.createReview(dto);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{id}/panic")
    public ResponseEntity<Void> panicRide(@PathVariable Long id) {
        rideService.panic(id);
        return ResponseEntity.ok().build();
    }
}
