package com.example.demo.services;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.request.CreateRideRequestDTO;
import com.example.demo.dto.request.RideCancellationRequestDTO;
import com.example.demo.dto.request.RideRequestUnregisteredDTO;
import com.example.demo.dto.response.RideEstimateResponseDTO;
import com.example.demo.dto.response.RideResponseDTO;
import com.example.demo.model.*;
import com.example.demo.repositories.*;
import com.example.demo.services.interfaces.RideService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    //Repository
    private final RideRepository rideRepository;
    private final LocationRepository locationRepository;
    private final RouteRepository routeRepository;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    // Ride creation
    @Override
    public RideResponseDTO createRide(CreateRideRequestDTO request) {

        // Validation
        if ((request.getOrigin().getLongitude() == request.getDestination().getLongitude()) &&
                (request.getOrigin().getLatitude() == request.getDestination().getLatitude()) &&
                (request.getOrigin().getAddress().equals(request.getDestination().getAddress()))) {
            try {
                throw new BadRequestException("Both origin and destination cannot be the same");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }

        LocalDateTime now = LocalDateTime.now();

        if (request.getScheduledTime().isAfter(now.plusHours(5))) {
            try {
                throw new BadRequestException("You cannot schedule a ride more than five hours in advance");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }

        // Create origin Location
        Location origin = new Location();
        origin.setLongitude(request.getOrigin().getLongitude());
        origin.setLatitude(request.getOrigin().getLatitude());
        origin.setAddress(request.getOrigin().getAddress());
        locationRepository.save(origin);

        // Create destination Location
        Location destination = new Location();
        destination.setLongitude(request.getDestination().getLongitude());
        destination.setLatitude(request.getDestination().getLatitude());
        destination.setAddress(request.getDestination().getAddress());
        locationRepository.save(destination);

        // Create route
        Route route = new Route();
        route.setOrigin(origin);
        route.setDestination(destination);
        route.setDistance(request.getDistanceKm());
        route.setDuration(request.getDurationMinutes());
        routeRepository.save(route);

        // Update drivers status based on work hours
        updateDriverStatuses();

        // Find suitable driver for ride
        List<Driver> drivers = driverRepository.filterAvailableDrivers(DriverStatus.ACTIVE ,request.isBabyFriendly(), request.isPetFriendly());
        Driver driver = findDriver(drivers);

        // Create ride
        Ride ride = new Ride();
        ride.setStatus(RideStatus.CREATED);
        ride.setScheduledTime(request.getScheduledTime());
        ride.setStops(createStops(request.getStops()));
        //ride.setLinkedPassengerEmails(request.getPassengerEmails());
        ride.setRoute(route);
        ride.setBabyFriendly(request.isBabyFriendly());
        ride.setPetFriendly(request.isPetFriendly());

        // Assign driver to ride
        if (driver == null) {
            ride.setStatus(RideStatus.FAILED); // later send notification
        }
        else {
            ride.setStatus(RideStatus.SCHEDULED);
            ride.setDriver(driver);
            driver.getScheduledRides().add(ride);
        }


        rideRepository.save(ride);

        // Map na response
        return new RideResponseDTO(
                ride.getId(),
                ride.getStatus(),
                ride.getPrice()
        );
    }

    // Create additional stops
    private List<Location> createStops(List<LocationDTO> incomingList) {
        if (incomingList == null) return null;

        List<Location> list = new ArrayList<>();
        for (LocationDTO loc : incomingList) {
            Location location = new Location();
            location.setLongitude(loc.getLongitude());
            location.setLatitude(loc.getLatitude());
            location.setAddress(loc.getAddress());
            locationRepository.save(location);
            list.add(location);
        }
        return list;
    }

    // Find suitable driver
    private Driver findDriver(List<Driver> drivers) {
        LocalDateTime now = LocalDateTime.now();
        int marginMinutes = 10;

        List<Driver> freeDrivers = new ArrayList<>();
        List<Driver> nearlyFreeDrivers = new ArrayList<>();

        for (Driver d : drivers) {
            // Completely free
            if (d.getScheduledRides().isEmpty()) {
                freeDrivers.add(d);
                continue;
            }

            // Has drives scheduled
            Ride nextRide = d.getScheduledRides().get(0);
            if (nextRide.getStatus() == RideStatus.STARTED) {
                LocalDateTime finishTime = nextRide.getScheduledTime().plusMinutes(nextRide.getRoute().getDuration());

                if (finishTime.isBefore(now.plusMinutes(marginMinutes))) {
                    nearlyFreeDrivers.add(d);
                }
            }
        }

        // Priority have free drivers
        if (!freeDrivers.isEmpty()) {
            return freeDrivers.get(0); // later will be updated based on position
        }

        // Then nearly free drivers
        if (!nearlyFreeDrivers.isEmpty()) {
            return nearlyFreeDrivers.get(0);
        }

        // No available drivers
        return null;
    }

    public void updateDriverStatuses() {
        int marginMinutes = 15;
        driverRepository.updateDriverStatus(8 * 60 + marginMinutes);
    }

    public RideEstimateResponseDTO estimateRide(RideRequestUnregisteredDTO request) {
        validateRideRequest(request);

        //Logic that will later be implemented
        RideEstimateResponseDTO response = new RideEstimateResponseDTO();
        return response;
    }

    private void validateRideRequest(RideRequestUnregisteredDTO request) {
        LocationDTO origin = request.getOrigin();
        LocationDTO destination = request.getDestination();

        if (origin.getLatitude() == destination.getLatitude() &&
                origin.getLongitude() == destination.getLongitude()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Origin and destination cannot be the same"
            );
        }
    }

    public void cancelRide(Long rideId, RideCancellationRequestDTO request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ride not found"
                ));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        if (user instanceof Driver) {

            if (request.getReason() == null || request.getReason().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Driver must provide cancellation reason"
                );
            }
        }

        if (user instanceof Passenger) {

            LocalDateTime limit = ride.getStartTime().minusMinutes(10);
            if (LocalDateTime.now().isAfter(limit)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Passenger can cancel only 10 minutes before ride start"
                );
            }
        }

        ride.setStatus(RideStatus.CANCELED);
        rideRepository.save(ride);
    }
}
