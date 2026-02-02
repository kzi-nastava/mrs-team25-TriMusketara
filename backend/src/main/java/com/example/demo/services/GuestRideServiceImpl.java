package com.example.demo.services;

import com.example.demo.dto.request.RideRequestUnregisteredDTO;
import com.example.demo.dto.response.GuestRideResponseDTO;
import com.example.demo.model.*;
import com.example.demo.repositories.DriverRepository;
import com.example.demo.repositories.GuestRideRepository;
import com.example.demo.repositories.LocationRepository;
import com.example.demo.repositories.RouteRepository;
import com.example.demo.services.interfaces.GuestRideService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestRideServiceImpl implements GuestRideService {
    private final GuestRideRepository guestRideRepository;
    private final LocationRepository locationRepository;
    private final RouteRepository routeRepository;
    private final DriverRepository driverRepository;

    @Transactional
    public GuestRideResponseDTO createGuestRide(RideRequestUnregisteredDTO request) {

        if (request.getOrigin().getLatitude() == request.getDestination().getLatitude() &&
                request.getOrigin().getLongitude() == request.getDestination().getLongitude()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Origin and destination cannot be the same");
        }

        Location origin = new Location();
        origin.setLatitude(request.getOrigin().getLatitude());
        origin.setLongitude(request.getOrigin().getLongitude());
        origin.setAddress(request.getOrigin().getAddress());
        locationRepository.save(origin);

        Location destination = new Location();
        destination.setLatitude(request.getDestination().getLatitude());
        destination.setLongitude(request.getDestination().getLongitude());
        destination.setAddress(request.getDestination().getAddress());
        locationRepository.save(destination);

        Route route = new Route();
        route.setOrigin(origin);
        route.setDestination(destination);
        double distanceKm = calculateDistance(origin, destination);
        int estimatedTime = estimateTime(distanceKm);

        route.setDistance(distanceKm);
        route.setEstimatedTime(estimatedTime);
        route.setDuration(estimatedTime);
        route.setTimesUsed(0);

        routeRepository.save(route);

        GuestRide ride = new GuestRide();
        ride.setRoute(route);
        ride.setStatus(RideStatus.CREATED);
        ride.setScheduledTime(LocalDateTime.now());

        List<Driver> availableDrivers = driverRepository.findAll();
        if (!availableDrivers.isEmpty()) {
            Driver driver = availableDrivers.get(0);
            ride.setDriver(driver);
            ride.setStatus(RideStatus.SCHEDULED);
        }

        guestRideRepository.save(ride);

        return new GuestRideResponseDTO(
                ride.getId(),
                ride.getStatus(),
                estimatedTime,
                distanceKm
        );
    }

    @Transactional
    public void cancelGuestRide(Long rideId) {
        GuestRide ride = guestRideRepository.findById(rideId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guest ride not found"));

        ride.setDriver(null);

        ride.setStatus(RideStatus.CANCELED);
        guestRideRepository.save(ride);
    }

    private double calculateDistance(Location origin, Location destination) {
        return 10.0; // km
    }

    private int estimateTime(double distanceKm) {
        return (int) Math.ceil(distanceKm / 40 * 60);
    }
}
