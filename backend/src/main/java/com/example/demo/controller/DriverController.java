package com.example.demo.controller;

import com.example.demo.dto.response.DriverRideHistoryResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    // 2.9.2: Driver history
    @GetMapping("/{id}/rides")
    public ResponseEntity<List<DriverRideHistoryResponseDTO>> getDriverHistory(@PathVariable Long id) {
        return ResponseEntity.ok(new ArrayList<>());
    }
}