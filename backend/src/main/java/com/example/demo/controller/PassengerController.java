package com.example.demo.controller;

import com.example.demo.dto.response.RouteFromFavoritesResponseDTO;
import com.example.demo.services.PassengerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/passenger")
public class PassengerController {

    private final PassengerServiceImpl passengerService;

    // Getting passengers list of favorite routes to display
    @GetMapping("/{passengerId}/favorite-routes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<RouteFromFavoritesResponseDTO>> getFavoriteRoutes(@PathVariable Long passengerId) {
        List<RouteFromFavoritesResponseDTO> favoriteRoutes = passengerService.getFavoriteRoutesForPassenger(passengerId);
        return ResponseEntity.ok(favoriteRoutes);
    }
}
