package com.example.demo.controller;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.dto.response.UserProfileResponseDTO;
import com.example.demo.model.Gender;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
public class UserController {

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

    // POST: Login korisnika
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(new LoginResponseDTO());
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
            @RequestBody UserRegistrationRequestDTO request
    ) {
        UserProfileResponseDTO response = new UserProfileResponseDTO();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET: Aktivacija korisnickog naloga
    @GetMapping("/auth/activate/{token}")
    public ResponseEntity<Void> activateUser(@PathVariable String token) {
        return ResponseEntity.ok().build();
    }
}
