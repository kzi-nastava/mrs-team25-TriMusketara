package com.example.demo.services;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.response.DriverRideHistoryResponseDTO;
import com.example.demo.dto.response.PassengerRideHistoryResponseDTO;
import com.example.demo.dto.response.RouteFromFavoritesResponseDTO;
import com.example.demo.dto.response.UserProfileResponseDTO;
import com.example.demo.model.*;
import com.example.demo.repositories.PassengerRepository;
import com.example.demo.repositories.RideRepository;
import com.example.demo.repositories.RouteRepository;
import com.example.demo.services.interfaces.PassengerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final RouteRepository routeRepository;
    private final RideRepository rideRepository;

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

    @Override
    public List<UserProfileResponseDTO> getAllPassengers() {
        List<Passenger> passengers = passengerRepository.findAll();

        return passengers.stream().map(this::mapPassengerToDTO).collect(Collectors.toList());
    }

    // Helper
    private UserProfileResponseDTO mapPassengerToDTO(Passenger passenger) {
        return new UserProfileResponseDTO(
                passenger.getId(),
                passenger.getEmail(),
                passenger.getName(),
                passenger.getSurname(),
                passenger.getAddress(),
                passenger.getPhone(),
                passenger.getProfileImageUrl(),
                passenger.isBlocked(),
                passenger.getBlockReason()
        );
    }

    @Override
    public List<PassengerRideHistoryResponseDTO> getPassengerRideHistory(Long passengerId) {

        List<Ride> rides = rideRepository.findAllByPassengerId(passengerId);
        List<PassengerRideHistoryResponseDTO> dtos = new ArrayList<>();

        for (Ride ride : rides) {

            if (ride.getStatus() != RideStatus.FINISHED
                    && ride.getStatus() != RideStatus.STOPPED) continue;

            PassengerRideHistoryResponseDTO dto = new PassengerRideHistoryResponseDTO();

            dto.setId(ride.getId());
            dto.setStartTime(ride.getStartTime());
            dto.setEndTime(ride.getEndTime());
            dto.setTotalPrice(ride.getPrice());

            if (ride.getDriver() != null) {
                dto.setDriverEmail(ride.getDriver().getEmail());
            }

            if (ride.getStatus() == RideStatus.FINISHED) {
                dto.setStatus("Completed");
            }
            if (ride.getStatus() == RideStatus.STOPPED) {
                dto.setStatus("Stopped");
            }

            if (ride.getRoute() != null) {
                Location start = ride.getRoute().getOrigin();
                Location end = ride.getRoute().getDestination();

                dto.setOrigin(new LocationDTO(
                        start.getLongitude(),
                        start.getLatitude(),
                        start.getAddress()
                ));

                dto.setDestination(new LocationDTO(
                        end.getLongitude(),
                        end.getLatitude(),
                        end.getAddress()
                ));
            }

            dtos.add(dto);
        }

        return dtos;
    }
}
