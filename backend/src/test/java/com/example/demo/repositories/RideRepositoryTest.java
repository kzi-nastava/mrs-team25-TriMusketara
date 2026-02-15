package com.example.demo.repositories;

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

    @Test
    void findAllByStatus_shouldReturnRides() {

        Ride ride = new Ride();
        ride.setStatus(RideStatus.STARTED);

        rideRepository.save(ride);

        List<Ride> rides = rideRepository.findAllByStatus(RideStatus.STARTED);

        assertFalse(rides.isEmpty());
    }
}
