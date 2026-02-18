package com.example.demo.services;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.response.AdminRideHistoryResponseDTO;
import com.example.demo.dto.response.AdminUserResponseDTO;
import com.example.demo.model.*;
import com.example.demo.repositories.GuestRideRepository;
import com.example.demo.repositories.RideRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.interfaces.AdminService;
import com.example.demo.services.interfaces.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final GuestRideRepository guestRideRepository;

    @Override
    public List<AdminRideHistoryResponseDTO> getRideHistory(Long userId, String userType, String sortBy) {

        List<AdminRideHistoryResponseDTO> dtos = new ArrayList<>();

        if ("driver".equalsIgnoreCase(userType)) {
            List<Ride> rides = rideRepository.findAllByDriverId(userId);
            for (Ride ride : rides) {
                if (ride.getStatus() != RideStatus.FINISHED && ride.getStatus() != RideStatus.STOPPED) continue;
                dtos.add(mapToAdminRideDTO(ride));
            }

            // GuestRide-ovi za vozača
            List<GuestRide> guestRides = guestRideRepository.findAllByDriverId(userId);
            for (GuestRide guestRide : guestRides) {
                if (guestRide.getStatus() != RideStatus.FINISHED && guestRide.getStatus() != RideStatus.STOPPED) continue;
                dtos.add(mapGuestRideToAdminDTO(guestRide));
            }

        } else if ("passenger".equalsIgnoreCase(userType)) {
            List<Ride> rides = rideRepository.findAllByPassengerId(userId);
            for (Ride ride : rides) {
                if (ride.getStatus() != RideStatus.FINISHED && ride.getStatus() != RideStatus.STOPPED) continue;
                dtos.add(mapToAdminRideDTO(ride));
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown user type: " + userType);
        }

        // Sortiranje na osnovu sortBy parametra
        dtos.sort((a, b) -> {
            switch (sortBy) {
                case "startTime":
                    return b.getStartTime().compareTo(a.getStartTime());
                case "endTime":
                    return b.getEndTime().compareTo(a.getEndTime());
                case "price":
                    return Double.compare(b.getTotalPrice(), a.getTotalPrice());
                case "status":
                    return b.getStatus().compareTo(a.getStatus());
                default:
                    return b.getStartTime().compareTo(a.getStartTime());
            }
        });

        return dtos;
    }



    private AdminRideHistoryResponseDTO mapToAdminRideDTO(Ride ride) {
        AdminRideHistoryResponseDTO dto = new AdminRideHistoryResponseDTO();
        dto.setId(ride.getId());
        dto.setStartTime(ride.getStartTime());
        dto.setEndTime(ride.getEndTime());
        dto.setTotalPrice(ride.getPrice());
        dto.setPanicPressed(ride.isPanicPressed());
        dto.setStatus(ride.getStatus() == RideStatus.FINISHED ? "Completed" : "Stopped");
        dto.setCancelledBy(ride.getCancelledBy() != null ? ride.getCancelledBy().getEmail() : null);

        if (ride.getRoute() != null) {
            dto.setOrigin(new LocationDTO(ride.getRoute().getOrigin().getLongitude(), ride.getRoute().getOrigin().getLatitude(), ride.getRoute().getOrigin().getAddress()));
            dto.setDestination(new LocationDTO(ride.getRoute().getDestination().getLongitude(), ride.getRoute().getDestination().getLatitude(), ride.getRoute().getDestination().getAddress()));
        }

        List<String> passengerEmails = ride.getPassengers().stream()
                .map(Passenger::getEmail)
                .toList();
        dto.setPassengerEmails(passengerEmails);

        return dto;
    }

    private AdminRideHistoryResponseDTO mapGuestRideToAdminDTO(GuestRide guestRide) {
        AdminRideHistoryResponseDTO dto = new AdminRideHistoryResponseDTO();
        dto.setId(guestRide.getId());
        dto.setStartTime(guestRide.getStartTime());
        dto.setEndTime(guestRide.getEndTime());
        dto.setTotalPrice(guestRide.getPrice());
        dto.setPanicPressed(guestRide.isPanicPressed());
        dto.setStatus(guestRide.getStatus() == RideStatus.FINISHED ? "COMPLETED" : "STOPPED");
        dto.setCancelledBy(null);
        dto.setPassengerEmails(null);

        if (guestRide.getRoute() != null) {
            dto.setOrigin(new LocationDTO(guestRide.getRoute().getOrigin().getLongitude(), guestRide.getRoute().getOrigin().getLatitude(), guestRide.getRoute().getOrigin().getAddress()));
            dto.setDestination(new LocationDTO(guestRide.getRoute().getDestination().getLongitude(), guestRide.getRoute().getDestination().getLatitude(), guestRide.getRoute().getDestination().getAddress()));
        }

        return dto;
    }


    public List<AdminUserResponseDTO> getAllNonAdminUsers() {

        List<User> users = userRepository.findAllNonAdmins();

        return users.stream()
                .map(user -> {

                    String role;

                    if (user instanceof Driver) {
                        role = "DRIVER";
                    } else if (user instanceof Passenger) {
                        role = "PASSENGER";
                    } else {
                        role = "UNKNOWN";
                    }

                    return new AdminUserResponseDTO(
                            user.getId(),
                            user.getEmail(),
                            role
                    );
                })
                .toList();
    }


}
