package com.example.demo.services;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.request.CompleteRegistrationRequestDTO;
import com.example.demo.dto.request.DriverRegistrationRequestDTO;
import com.example.demo.dto.response.DriverRegistrationResponseDTO;
import com.example.demo.dto.response.VehicleResponseDTO;
import com.example.demo.model.Driver;
import com.example.demo.model.DriverStatus;
import com.example.demo.model.EmailDetails;
import com.example.demo.model.Vehicle;
import com.example.demo.dto.response.DriverRideHistoryResponseDTO;
import com.example.demo.model.*;
import com.example.demo.repositories.DriverRepository;
import com.example.demo.repositories.RideRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.VehicleRepository;
import com.example.demo.services.interfaces.DriverService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    // Inject repository
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final EmailServiceImpl emailService;
    private final PasswordEncoder passwordEncoder;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    @Override
    public List<DriverRideHistoryResponseDTO> getDriverRideHistory(Long driverId) {
        // --- SECURITY ---
        if (!isOwnerOrAdmin(driverId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nemate pristup ovim podacima.");
        }

        List<Ride> rides = rideRepository.findAllByDriverId(driverId);
        List<DriverRideHistoryResponseDTO> dtos = new ArrayList<>();

        for (Ride ride : rides) {
            if (ride.getStatus() != RideStatus.FINISHED) continue;

            // DTO Mapping
            DriverRideHistoryResponseDTO dto = new DriverRideHistoryResponseDTO();
            dto.setId(ride.getId());
            dto.setStartTime(ride.getStartTime());
            dto.setEndTime(ride.getEndTime());
            dto.setTotalPrice(ride.getPrice());
            dto.setPanicPressed(ride.isPanicPressed());

            if (ride.getRoute() != null) {
                Location start = ride.getRoute().getOrigin();
                Location end = ride.getRoute().getDestination();
                dto.setOrigin(new LocationDTO(start.getLongitude(), start.getLatitude(), start.getAddress()));
                dto.setDestination(new LocationDTO(end.getLongitude(), end.getLatitude(), end.getAddress()));
            }
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public DriverRegistrationResponseDTO registerDriver(DriverRegistrationRequestDTO request) {

        // Validation
        if (driverRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // Create Vehicle object
        Vehicle vehicle = new Vehicle();
        vehicle.setModel(request.getVehicle().getModel());
        vehicle.setType(request.getVehicle().getType());
        vehicle.setRegistration(request.getVehicle().getRegistration());
        vehicle.setSeats(request.getVehicle().getSeats());
        vehicle.setIsBabyFriendly(request.getVehicle().isBabyFriendly());
        vehicle.setIsPetFriendly(request.getVehicle().isPetFriendly());
        vehicleRepository.save(vehicle);

        // Create Driver object
        Driver driver = new Driver();
        driver.setName(request.getName());
        driver.setSurname(request.getSurname());
        driver.setEmail(request.getEmail());
        // Temporary password until driver changes it
        driver.setPassword(UUID.randomUUID().toString());
        driver.setGender(request.getGender());
        driver.setAddress(request.getAddress());
        driver.setPhone(request.getPhone());
        driver.setStatus(DriverStatus.PENDING);
        driver.setVehicle(vehicle); // set vehicle
        // Random token to keep track which driver is being registered
        driver.setRegistrationToken(UUID.randomUUID().toString());

        // Write in database
        Driver saved = driverRepository.save(driver);

        // Sending driver email to set up password
        EmailDetails email = new EmailDetails();
        email.setRecipient(driver.getEmail());
        email.setSubject("Complete your registration");
        email.setMsgBody(
                "Hello " + driver.getName() + ",\n\n" +
                        "Welcome to ClickAndDrive! Please complete your registration by setting your password:\n\n" +
                        "http://localhost:4200/complete-registration?token=" + driver.getRegistrationToken() + "\n\n" +
                        "This link will expire in 24 hours.\n\n" +
                        "Best regards,\nClickAndDrive Team"
        );

        emailService.sendsSimpleMail(email);

        // Map object to response
        return new DriverRegistrationResponseDTO(
                saved.getId(),
                saved.getEmail(),
                saved.getName(),
                saved.getSurname(),
                saved.getStatus()
        );
    }

    @Override
    public void completeRegistration(CompleteRegistrationRequestDTO request) {
        // Validation
        // Are password and confirmPassword the same
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            try {
                throw new BadRequestException("Password and confirm password are not the same");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }

        // Find driver by registration token
        try {
            Driver driver = driverRepository.findByRegistrationToken(request.getToken()).orElseThrow(() -> new BadRequestException("Invalid or expired token"));

            // Validation
            // Check if password is already set
            if (driver.getRegistrationToken() == null) {
                throw new BadRequestException("You are already registered");
            }

            // Set password
            driver.setPassword(passwordEncoder.encode(request.getPassword()));

            // Delete registration token
            driver.setRegistrationToken(null);

            driver.setStatus(DriverStatus.ACTIVE);
            driver.setWorkMinutes(0);

            driverRepository.save(driver);

            // Send email to confirm successful registration
            EmailDetails confirmEmail = new EmailDetails();
            confirmEmail.setRecipient(driver.getEmail());
            confirmEmail.setSubject("Registration completed");
            confirmEmail.setMsgBody(
                    "Hello " + driver.getName() + ",\n\n" +
                            "Your registration is now complete! You can log in to ClickAndDrive.\n\n" +
                            "Best regards,\nClickAndDrive Team"
            );
            emailService.sendsSimpleMail(confirmEmail);

        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        return driverRepository.findByRegistrationToken(token).isPresent();
    }

    // Long id is drivers id
    @Override
    public VehicleResponseDTO getDriverVehicle(Long id) {
        // First find the driver
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new RuntimeException("No driver with id exists"));

        // Fetch his vehicle
        Vehicle vehicle = driver.getVehicle();

        return new VehicleResponseDTO(
                vehicle.getId(),
                vehicle.getModel(),
                vehicle.getType(),
                vehicle.getRegistration(),
                vehicle.getSeats(),
                vehicle.getIsBabyFriendly(),
                vehicle.getIsPetFriendly()
        );
    }

    public boolean isOwnerOrAdmin(Long idFromPath) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (principal instanceof User) {
            User loggedInUser = (User) principal;

            // Debug
//            System.out.println("Ulogovan korisnik ID: " + loggedInUser.getId());
//            System.out.println("ID iz URL-a: " + idFromPath);

            // Admin?
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
            if (isAdmin) return true;

            // Owner?
            return loggedInUser.getId().equals(idFromPath);
        }

        return false;
    }
}
