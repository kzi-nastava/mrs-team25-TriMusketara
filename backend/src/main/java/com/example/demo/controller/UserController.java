package com.example.demo.controller;

import com.example.demo.dto.request.UpdateUserProfileRequestDTO;
import com.example.demo.dto.response.UserProfileResponseDTO;
import com.example.demo.model.Gender;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // GET
    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(
            @PathVariable Long id) {

        // Logic for loading a user
        UserProfileResponseDTO response = new UserProfileResponseDTO(
                id,
                "user@gmail.com",
                "John",
                "Doe",
                Gender.MALE,
                "Hueco Mundo",
                "+123456789"
        );

        return ResponseEntity.ok(response);
    }


    // PUT, information change
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserProfileResponseDTO> updateUserProfile(
            @PathVariable Long id,
            @RequestBody UpdateUserProfileRequestDTO request) {

        // Update user info
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
}
