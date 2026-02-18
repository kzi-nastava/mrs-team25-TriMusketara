package com.example.demo.services;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.response.AdminRideHistoryResponseDTO;
import com.example.demo.dto.response.AdminUserResponseDTO;
import com.example.demo.model.*;
import com.example.demo.repositories.RideRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.interfaces.AdminService;
import com.example.demo.services.interfaces.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    @Override
    public List<AdminRideHistoryResponseDTO> getRideHistory(
            Long id,
            String role,
            LocalDateTime from,
            LocalDateTime to,
            String sortBy) {

        List<Ride> rides;

        if (role.equalsIgnoreCase("DRIVER")) {
            rides = rideRepository.findAllByDriverIdOrderByStartTimeDesc(id);
        } else {
            rides = rideRepository.findAllByPassengerIdOrdered(id);
        }

        return rides.stream()
                .filter(r -> {
                    if (from != null && r.getStartTime().isBefore(from)) return false;
                    if (to != null && r.getStartTime().isAfter(to)) return false;
                    return true;
                })
                .sorted((r1, r2) -> {
                    if ("price".equalsIgnoreCase(sortBy)) {
                        return Double.compare(r2.getPrice(), r1.getPrice());
                    }
                    return r2.getStartTime().compareTo(r1.getStartTime());
                })
                .map(this::mapToDTO)
                .toList();
    }

    private AdminRideHistoryResponseDTO mapToDTO(Ride ride) {

        AdminRideHistoryResponseDTO dto = new AdminRideHistoryResponseDTO();

        dto.setId(ride.getId());
        dto.setStartTime(ride.getStartTime());
        dto.setEndTime(ride.getEndTime());
        dto.setTotalPrice(ride.getPrice());
        dto.setPanicPressed(ride.isPanicPressed());

        dto.setCancelled(ride.getStatus() == RideStatus.CANCELED);

        if (ride.getCancelledBy() != null) {
            dto.setCancelledBy(ride.getCancelledBy().getEmail());
        }

        if (ride.getDriver() != null) {
            dto.setDriverEmail(ride.getDriver().getEmail());
        }

        dto.setPassengerEmails(
                ride.getPassengers()
                        .stream()
                        .map(p -> p.getEmail())
                        .toList()
        );

        if (ride.getRoute() != null) {
            dto.setOrigin(new LocationDTO(
                    ride.getRoute().getOrigin().getLongitude(),
                    ride.getRoute().getOrigin().getLatitude(),
                    ride.getRoute().getOrigin().getAddress()
            ));

            dto.setDestination(new LocationDTO(
                    ride.getRoute().getDestination().getLongitude(),
                    ride.getRoute().getDestination().getLatitude(),
                    ride.getRoute().getDestination().getAddress()
            ));
        }

        dto.setStatus(ride.getStatus().name());

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
