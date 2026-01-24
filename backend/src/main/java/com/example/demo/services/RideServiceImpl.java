package com.example.demo.services;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.request.CreateRideRequestDTO;
import com.example.demo.dto.response.RideResponseDTO;
import com.example.demo.model.Location;
import com.example.demo.model.Ride;
import com.example.demo.model.RideStatus;
import com.example.demo.model.Route;
import com.example.demo.repositories.LocationRepository;
import com.example.demo.repositories.RideRepository;
import com.example.demo.repositories.RouteRepository;
import com.example.demo.services.interfaces.RideService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

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
        routeRepository.save(route);

        // Create ride
        Ride ride = new Ride();
        ride.setStatus(RideStatus.CREATED);
        ride.setScheduledTime(request.getScheduledTime());
        ride.setStops(createStops(request.getStops()));
        ride.setRoute(route);
        ride.setBabyFriendly(request.isBabyFriendly());
        ride.setPetFriendly(request.isPetFriendly());

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
            list.add(location);
        }
        return list;
    }

}
