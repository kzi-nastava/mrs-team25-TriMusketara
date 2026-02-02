package com.example.demo.services;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.response.RouteFromFavoritesResponseDTO;
import com.example.demo.model.Passenger;
import com.example.demo.model.Route;
import com.example.demo.repositories.PassengerRepository;
import com.example.demo.repositories.RouteRepository;
import com.example.demo.services.interfaces.PassengerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final RouteRepository routeRepository;

    @Override
    public List<RouteFromFavoritesResponseDTO> getFavoriteRoutesForPassenger(Long passengerId) {
        // Find the passenger by id
        Passenger passenger = passengerRepository.findById(passengerId).orElseThrow(() -> new RuntimeException("Passenger not found"));

        // Get his favorite routes, if he has any
        List<Route> favoriteRoutes = passenger.getFavoriteRoutes();

        // Map models to ResponseDTO and send to frontend
        return favoriteRoutes.stream()
                .map(route -> new RouteFromFavoritesResponseDTO(
                        route.getId(),
                        new LocationDTO(route.getOrigin().getLongitude(), route.getOrigin().getLatitude(), route.getOrigin().getAddress()),
                        new LocationDTO(route.getDestination().getLongitude(), route.getDestination().getLatitude(), route.getDestination().getAddress()),
                        route.getDistance(),
                        route.getDuration(),
                        route.getTimesUsed()
                )).toList();
    }

    @Override
    @Transactional
    public void removeFromFavoriteRoutes(Long passengerId, Long routeId) {
        // Find passenger from id
        Passenger passenger = passengerRepository.findById(passengerId).orElseThrow(() -> new RuntimeException("Passenger not found"));

        boolean removed = passenger.getFavoriteRoutes()
                        .removeIf(route -> route.getId().equals(routeId));

        passengerRepository.save(passenger);
    }
}
