package com.example.demo.repositories;

import com.example.demo.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    // Check to see if an email already exists
    boolean existsByEmail(String email);
    // Find driver by registration token
    Optional<Driver> findByRegistrationToken(String token);
}
