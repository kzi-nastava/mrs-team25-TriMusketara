package com.example.demo.services;

import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.LoginRequestDTO;
import com.example.demo.dto.request.UpdateUserProfileRequestDTO;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.dto.response.UserProfileResponseDTO;
import com.example.demo.model.*;
import com.example.demo.repositories.*;
import com.example.demo.security.JwtUtil;
import com.example.demo.services.interfaces.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final VehicleRepository vehicleRepository;

    public UserServiceImpl(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            JwtUtil jwtUtil, VehicleRepository vehicleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.vehicleRepository = vehicleRepository;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        String role = user.getClass().getSimpleName().toLowerCase();
        if (role.equals("administrator")) {
            role = "admin";
        } else if (role.equals("passenger")) {
            role = "user";
        } else if (role.equals("driver")) {
            role = "driver";
        }

        String token = jwtUtil.generateToken(user);

        return new LoginResponseDTO(user.getId(), user.getEmail(), role, token);
    }

    public UserProfileResponseDTO getUserProfile(Long id) {
        // Find user in database based on his id
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("No user with this id exists"));

        // Map user data to response
        return new UserProfileResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getSurname(),
                user.getAddress(),
                user.getPhone()
        );
    }

    @Transactional
    @Override
    public UserProfileResponseDTO changeUserInfo(Long id, UpdateUserProfileRequestDTO request) {
        // Get user by id from database
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("No user with this id exists"));

        // Set changed information
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());

        // Email check and validation
        if (!user.getEmail().equals(request.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        // Check users role, if driver, update his vehicle information as well
        String role = user.getClass().getSimpleName().toLowerCase();
        role = switch (role) {
            case "administrator" -> "admin";
            case "passenger" -> "user";
            case "driver" -> "driver";
            default -> role;
        };

        if (role.equals("driver") && request.getVehicle() != null) {
            Driver driver = (Driver) user;

            if (driver.getVehicle() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver does not have a vehicle assigned");
            }

            Vehicle vehicle = driver.getVehicle();
            // Update vehicle information
            vehicle.setModel(request.getVehicle().getModel());
            vehicle.setType(request.getVehicle().getType());
            vehicle.setIsBabyFriendly(request.getVehicle().getIsBabyFriendly());
            vehicle.setIsPetFriendly(request.getVehicle().getIsPetFriendly());

            // Validate registration, and see if unique
            if (!driver.getVehicle().getRegistration().equals(request.getVehicle().getRegistration())) {
                boolean existsRegistration = vehicleRepository.existsByRegistration(request.getVehicle().getRegistration());
                if (existsRegistration) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Registration table already exists");
                }
                vehicle.setRegistration(request.getVehicle().getRegistration());
            }
            // Save vehicle
            vehicleRepository.save(vehicle);
        }
        // Save user
        userRepository.save(user);

        return new UserProfileResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getSurname(),
                user.getAddress(),
                user.getPhone()
        );
    }

    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        // Find user
        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Check if current password matches the one in database
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        // Check if new password and confirmation are equal
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New passwords do not match");
        }

        // Save new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
