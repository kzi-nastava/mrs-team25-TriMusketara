package com.example.demo.repositories;

import com.example.demo.model.Driver;
import com.example.demo.model.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    // Check to see if an email already exists
    boolean existsByEmail(String email);

    // Find driver by registration token
    Optional<Driver> findByRegistrationToken(String token);

    // Update driver status to INACTIVE if he is overworked
    @Modifying
    @Transactional
    @Query("UPDATE Driver d SET d.status = 'INACTIVE' WHERE d.workMinutes > :maxMinutes AND d.status = 'ACTIVE'")
    void updateDriverStatus(@Param("maxMinutes") int maxMinutes);

    // Filter drivers by their status and if they are baby/pet friendly
    @Query("""
        SELECT DISTINCT d FROM Driver d
        LEFT JOIN FETCH d.vehicle
        LEFT JOIN FETCH d.scheduledRides
        WHERE d.status = :status
          AND d.vehicle.isBabyFriendly = :baby_bool
          AND d.vehicle.isPetFriendly = :pet_bool
        """)
    List<Driver> filterAvailableDrivers(@Param("status") DriverStatus status, @Param("baby_bool") boolean baby_bool, @Param("pet_bool") boolean pet_bool);
}
