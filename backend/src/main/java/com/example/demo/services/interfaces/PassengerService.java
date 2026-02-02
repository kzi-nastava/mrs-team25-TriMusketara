package com.example.demo.services.interfaces;

import com.example.demo.dto.response.RouteFromFavoritesResponseDTO;

import java.util.List;

public interface PassengerService {
    List<RouteFromFavoritesResponseDTO> getFavoriteRoutesForPassenger(Long passengerId);
    void removeFromFavoriteRoutes(Long passengerId, Long routeId);
}
