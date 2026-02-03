package com.example.demo.repositories;

import com.example.demo.model.GuestRide;
import com.example.demo.model.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findAllByDriverId(Long driverId);
    Page<Ride> findAllByDriverId(Long driverId, Pageable pageable);
}
