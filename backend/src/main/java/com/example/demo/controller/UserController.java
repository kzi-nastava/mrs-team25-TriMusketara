package com.example.demo.controller;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.dto.response.UserProfileResponseDTO;
import com.example.demo.model.Administrator;
import com.example.demo.model.Gender;
import com.example.demo.model.Passenger;
import com.example.demo.model.User;
import com.example.demo.repositories.AdministratorRepository;
import com.example.demo.repositories.PassengerRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.services.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.services.interfaces.UserService;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final AdministratorRepository administratorRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;

    public UserController(UserService userService, AdministratorRepository administratorRepository, JwtUtil jwtUtil, UserRepository userRepository, PassengerRepository passengerRepository){
        this.userService = userService;
        this.administratorRepository = administratorRepository;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passengerRepository = passengerRepository;
    }

    // GET profile
    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@PathVariable Long id) {

        UserProfileResponseDTO response = new UserProfileResponseDTO(
                id,
                "user@google.com",
                "Jhon",
                "Doe",
                Gender.MALE,
                "Hueco Mundo",
                "+123456789"
        );
        return ResponseEntity.ok(response);
    }

    // PUT profile change
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserProfileResponseDTO> updateUserProfile(
            @PathVariable Long id,
            @RequestBody UpdateUserProfileRequestDTO request) {
        UserProfileResponseDTO response = new UserProfileResponseDTO(
                id,
                "user@google.com",
                request.getName(),
                request.getSurname(),
                Gender.MALE,
                request.getAddress(),
                request.getPhone()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponseDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = jwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LoginResponseDTO response = new LoginResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getClass().getSimpleName(),
                token
        );

        return ResponseEntity.ok(response);
    }

    // POST: Login korisnika
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/auth/test")
    public String test() {
        return "Test endpoint radi!";
    }

    @GetMapping("/auth/secure-test")
    public String secureTest() {
        return "Secure endpoint radi, JWT potreban!";
    }

    @PostMapping("/test-admin")
    public String testAdmin() {
        Administrator admin = administratorRepository.findAll().get(0);
        return admin.getEmail();
    }

    // POST: Logout korisnika
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    // POST: Zaboravljena lozinka
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        return ResponseEntity.ok().build();
    }

    // POST: Reset lozinke
    @PostMapping("/auth/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        return ResponseEntity.ok().build();
    }

    // PATCH: Promena statusa vozača
    @PatchMapping("/drivers/{id}/status")
    public ResponseEntity<Void> changeDriverStatus(
            @PathVariable Long id,
            @RequestBody DriverStatusRequestDTO request
    ) {
        return ResponseEntity.ok().build();
    }

    // POST: Registracija korisnika
    @PostMapping("/auth/register")
    public ResponseEntity<UserProfileResponseDTO> registerUser(
            @Valid @RequestBody UserRegistrationRequestDTO request
    ) {
        UserProfileResponseDTO response = userService.registerPassenger(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET: Aktivacija korisnickog naloga
    @GetMapping("/auth/activate/{token}")
    public ResponseEntity<Void> activateUser(@PathVariable String token) {
        userService.activatePassenger(token);
        return ResponseEntity.ok().build();
    }
}
