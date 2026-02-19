package com.example.demo.repositories;

import com.example.demo.model.Driver;
import com.example.demo.model.Passenger;
import com.example.demo.model.Ride;
import com.example.demo.model.RideStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ActiveProfiles("test")
public class RideRepositoryTest {
    @Autowired
    private RideRepository rideRepository;

    /*
    * List<Ride> findAllByPassengerIdOrdered(@Param("passengerId") Long passengerId);*/

    @Test
    void findAllByStatus_shouldReturnRides() {

        Ride ride = new Ride();
        ride.setStatus(RideStatus.STARTED);

        rideRepository.save(ride);

        List<Ride> rides = rideRepository.findAllByStatus(RideStatus.STARTED);

        assertFalse(rides.isEmpty());
    }

    @Test
    void findAllByDriverId_shouldReturnRides() {

        Driver driver = new Driver();

        Ride ride = new Ride();
        ride.setDriver(driver);

        rideRepository.save(ride);

        List<Ride> rides = rideRepository.findAllByDriverId(driver.getId());

        assertFalse(rides.isEmpty());
    }

    @Test
    void findAllByPassengerId_shouldReturnRides() {

        Passenger creator = new Passenger();
        Ride ride = new Ride();

        ride.setRideCreator(creator);

        rideRepository.save(ride);

        List<Ride> rides = rideRepository.findAllByPassengerId(creator.getId());

        assertFalse(rides.isEmpty());
    }
}
