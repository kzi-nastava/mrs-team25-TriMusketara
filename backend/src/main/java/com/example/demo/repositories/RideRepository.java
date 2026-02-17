package com.example.demo.repositories;

import com.example.demo.model.GuestRide;
import com.example.demo.model.Ride;
import com.example.demo.model.RideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findAllByDriverId(Long driverId);
    Page<Ride> findAllByDriverId(Long driverId, Pageable pageable);


    @Query("SELECT r FROM Ride r JOIN r.passengers p WHERE p.id = :passengerId")
    List<Ride> findAllByPassengerId(@Param("passengerId") Long passengerId);

    List<Ride> findAllByStatus(RideStatus rideStatus);
}
