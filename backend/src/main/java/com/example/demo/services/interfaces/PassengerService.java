package com.example.demo.services.interfaces;

import com.example.demo.dto.response.RouteFromFavoritesResponseDTO;
import com.example.demo.dto.response.UserProfileResponseDTO;

import java.util.List;

public interface PassengerService {
    List<RouteFromFavoritesResponseDTO> getFavoriteRoutesForPassenger(Long passengerId);
    void removeFromFavoriteRoutes(Long passengerId, Long routeId);
    List<UserProfileResponseDTO> getAllPassengers();
}
