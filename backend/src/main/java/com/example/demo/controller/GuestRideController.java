package com.example.demo.controller;

import com.example.demo.dto.request.RideRequestUnregisteredDTO;
import com.example.demo.dto.response.GuestRideResponseDTO;
import com.example.demo.services.interfaces.GuestRideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest-rides")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class GuestRideController {

    private final GuestRideService guestRideService;

    // Kreiranje guest ride
    @PostMapping("/create")
    public ResponseEntity<GuestRideResponseDTO> createGuestRide(
            @RequestBody RideRequestUnregisteredDTO request) {

        GuestRideResponseDTO response = guestRideService.createGuestRide(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Cancel guest ride
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelGuestRide(@PathVariable Long id) {
        guestRideService.cancelGuestRide(id);
        return ResponseEntity.ok().build();
    }
}
