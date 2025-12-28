package com.example.demo.controller;

import com.example.demo.dto.request.DriverRegistrationRequestDTO;
import com.example.demo.dto.response.DriverRegistrationResponseDTO;
import com.example.demo.model.DriverStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
