package com.example.demo.services;

import com.example.demo.dto.request.LoginRequestDTO;
import com.example.demo.dto.response.LoginResponseDTO;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
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
}
