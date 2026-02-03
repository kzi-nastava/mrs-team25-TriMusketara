package com.example.demo.repositories;

import com.example.demo.model.GuestRide;
import com.example.demo.model.Ride;
import com.example.demo.model.RideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRideRepository extends JpaRepository<GuestRide, Long> {
    List<GuestRide> findAllByStatus(RideStatus status);
    List<GuestRide> findAllByDriverId(Long driverId);
    Page<GuestRide> findAllByDriverId(Long driverId, Pageable pageable);
}