package com.example.demo.services;

import com.example.demo.dto.request.LoginRequestDTO;
import com.example.demo.dto.request.UserRegistrationRequestDTO;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.dto.response.UserProfileResponseDTO;
import com.example.demo.model.Administrator;
import com.example.demo.model.Driver;
import com.example.demo.model.Passenger;
import com.example.demo.model.Gender;
import com.example.demo.model.User;
import com.example.demo.repositories.AdministratorRepository;
import com.example.demo.repositories.DriverRepository;
import com.example.demo.repositories.PassengerRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            PassengerRepository passengerRepository,
            JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.passengerRepository = passengerRepository;
        this.jwtUtil = jwtUtil;
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

    public UserProfileResponseDTO registerPassenger(UserRegistrationRequestDTO dto) {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Passwords do not match"
            );
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email already exists"
            );
        }

        Passenger passenger = new Passenger();
        passenger.setName(dto.getName());
        passenger.setSurname(dto.getLastName());
        passenger.setEmail(dto.getEmail());
        passenger.setPassword(passwordEncoder.encode(dto.getPassword()));
        passenger.setAddress(dto.getAddress());
        passenger.setPhone(dto.getPhoneNumber());
        passenger.setGender(Gender.MALE);

        Passenger saved = passengerRepository.save(passenger);

        return new UserProfileResponseDTO(
                saved.getId(),
                saved.getEmail(),
                saved.getName(),
                saved.getSurname(),
                saved.getGender(),
                saved.getAddress(),
                saved.getPhone()
        );
    }

}
