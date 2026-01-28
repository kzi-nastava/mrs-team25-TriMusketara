package com.example.demo.services;

import com.example.demo.dto.request.LoginRequestDTO;
import com.example.demo.dto.request.UserRegistrationRequestDTO;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.dto.response.UserProfileResponseDTO;
import com.example.demo.model.*;
import com.example.demo.repositories.AdministratorRepository;
import com.example.demo.repositories.DriverRepository;
import com.example.demo.repositories.PassengerRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.services.interfaces.EmailService;
import com.example.demo.services.interfaces.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public UserServiceImpl(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            PassengerRepository passengerRepository,
            JwtUtil jwtUtil,
            EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.passengerRepository = passengerRepository;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user instanceof Passenger passenger && !passenger.isActivated()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account not activated. Check your email.");
        }

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

        if (passengerRepository.findByEmail(dto.getEmail()).isPresent()) {
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

        passenger.setActivated(false);
        passenger.setActivationToken(java.util.UUID.randomUUID().toString());
        passenger.setActivationTokenExpiry(LocalDateTime.now().plusHours(24));

        Passenger saved = passengerRepository.save(passenger);


        System.out.println(saved.getEmail());
        EmailDetails email = new EmailDetails();
        email.setRecipient(saved.getEmail());
        email.setSubject("Activate your account");
        email.setMsgBody(
                "Hello " + saved.getName() + ",\n\n" +
                        "Welcome to ClickAndDrive! Please complete your registration by setting your password:\n\n" +
                        "http://localhost:4200/activate-account?token=" + saved.getActivationToken() + "\n\n" +
                        "This link will expire in 24 hours.\n\n" +
                        "Best regards,\nClickAndDrive Team"
        );

        emailService.sendsSimpleMail(email);

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

    @Transactional
    public boolean activatePassenger(String token) {
        Passenger passenger = passengerRepository.findByActivationToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token"));

        passenger.setActivated(true);
        passenger.setActivationToken(null);
        passenger.setActivationTokenExpiry(null);
        passengerRepository.save(passenger);

        return true;
    }
}
