package com.example.demo.controller;

import com.example.demo.dto.request.DriverRegistrationRequestDTO;
import com.example.demo.dto.response.DriverRegistrationResponseDTO;
import com.example.demo.model.DriverStatus;
import com.example.demo.dto.response.RideDetailsResponseDTO;
import com.example.demo.dto.response.RideHistoryResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
}
