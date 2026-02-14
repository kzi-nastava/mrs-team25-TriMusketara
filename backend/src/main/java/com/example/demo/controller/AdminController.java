package com.example.demo.controller;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.request.NoteRequestDTO;
import com.example.demo.dto.request.DriverRegistrationRequestDTO;
import com.example.demo.dto.response.*;
import com.example.demo.dto.VehiclePriceDTO;

import com.example.demo.services.interfaces.DriverService;
import com.example.demo.services.interfaces.PanicService;
import com.example.demo.services.interfaces.PassengerService;
import com.example.demo.services.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AdminController {

    // Services
    private final DriverService driverService;
    private final PassengerService passengerService;
    private final UserService userService;
    private final PanicService panicService;

    @PostMapping("/drivers")
    public ResponseEntity<DriverRegistrationResponseDTO> registerDriver(
            @Valid @RequestBody DriverRegistrationRequestDTO request,
            @RequestParam(defaultValue = "web") String platform) {

        // Call service function to register a new driver into the app
        DriverRegistrationResponseDTO response = driverService.registerDriver(request, platform);

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

    // GET all drivers
    @GetMapping("/drivers/all")
    public ResponseEntity<List<UserProfileResponseDTO>> getAllDrivers() {
        List<UserProfileResponseDTO> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    // GET all passengers
    @GetMapping("/passengers/all")
    public ResponseEntity<List<UserProfileResponseDTO>> getAllPassengers() {
        List<UserProfileResponseDTO> passengers = passengerService.getAllPassengers();
        return ResponseEntity.ok(passengers);
    }

    // Block a user (driver or passenger)
    @PutMapping("/users/{id}/block")
    public ResponseEntity<UserProfileResponseDTO> blockUser(@PathVariable Long id, @RequestBody(required = false) NoteRequestDTO request) {
        String reason = (request != null) ? request.getMessage() : null;
        UserProfileResponseDTO updatedUser = userService.blockUser(id, reason);

        return ResponseEntity.ok(updatedUser);
    }

    // Unblock a user
    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<UserProfileResponseDTO> unblockUser(@PathVariable Long id) {
        UserProfileResponseDTO updatedUser = userService.unblockUser(id);
        return ResponseEntity.ok(updatedUser);
    }

    // Leave a note
    @PutMapping("/users/{id}/note")
    public ResponseEntity<UserProfileResponseDTO> leaveNote(@PathVariable Long id, @RequestBody NoteRequestDTO request) {
        UserProfileResponseDTO updatedUser = userService.setNote(id, request.getMessage());
        return ResponseEntity.ok(updatedUser);
    }

}
