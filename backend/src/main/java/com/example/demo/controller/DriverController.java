package com.example.demo.controller;

import com.example.demo.dto.request.CompleteRegistrationRequestDTO;
import com.example.demo.dto.response.DriverRideHistoryResponseDTO;
import com.example.demo.services.interfaces.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class DriverController {

    // Service
    private final DriverService driverService;

    // 2.9.2: Driver history
    @GetMapping("/{id}/rides")
    public ResponseEntity<List<DriverRideHistoryResponseDTO>> getDriverHistory(@PathVariable Long id) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @PostMapping("/complete-registration")
    public ResponseEntity<String> completeRegistration(@Valid @RequestBody CompleteRegistrationRequestDTO request) {

        driverService.completeRegistration(request);
        return ResponseEntity.ok("Registration is successful");
    }
}