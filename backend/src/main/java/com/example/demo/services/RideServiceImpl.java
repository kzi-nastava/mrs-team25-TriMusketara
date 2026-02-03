package com.example.demo.services;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;
import com.example.demo.model.*;
import com.example.demo.repositories.*;
import com.example.demo.services.interfaces.EmailService;
import com.example.demo.services.interfaces.RideService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    //Repository
    private final RideRepository rideRepository;
    private final GuestRideRepository guestRideRepository;
    private final LocationRepository locationRepository;
    private final RouteRepository routeRepository;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final PanicRepository panicRepository;
    private final PassengerRepository passengerRepository;
    private final InconsistencyReportRepository inconsistencyReportRepository;
    //Service
    private final EmailService emailService;

    // Ride creation
    @Override
    public RideResponseDTO createRide(CreateRideRequestDTO request) {

        // Validation
        if ((request.getOrigin().getLongitude() == request.getDestination().getLongitude()) &&
                (request.getOrigin().getLatitude() == request.getDestination().getLatitude()) &&
                (request.getOrigin().getAddress().equals(request.getDestination().getAddress()))) {
            try {
                throw new BadRequestException("Both origin and destination cannot be the same");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }

        LocalDateTime now = LocalDateTime.now();

        if (request.getScheduledTime().isAfter(now.plusHours(5))) {
            try {
                throw new BadRequestException("You cannot schedule a ride more than five hours in advance");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }

        // Create origin Location
        Location origin = new Location();
        origin.setLongitude(request.getOrigin().getLongitude());
        origin.setLatitude(request.getOrigin().getLatitude());
        origin.setAddress(request.getOrigin().getAddress());
        locationRepository.save(origin);

        // Create destination Location
        Location destination = new Location();
        destination.setLongitude(request.getDestination().getLongitude());
        destination.setLatitude(request.getDestination().getLatitude());
        destination.setAddress(request.getDestination().getAddress());
        locationRepository.save(destination);

        // Create route
        Route route = new Route();
        route.setOrigin(origin);
        route.setDestination(destination);
        route.setDistance(request.getDistanceKm());
        route.setDuration(request.getDurationMinutes());
        routeRepository.save(route);

        // Update drivers status based on work hours
        updateDriverStatuses();

        // Find suitable driver for ride
        List<Driver> drivers = driverRepository.filterAvailableDrivers(DriverStatus.ACTIVE ,request.isBabyFriendly(), request.isPetFriendly());
        Driver driver = findDriver(drivers);

        // Create ride
        Ride ride = new Ride();
        ride.setStatus(RideStatus.CREATED);
        ride.setScheduledTime(request.getScheduledTime());
        ride.setStops(createStops(request.getStops()));
        //ride.setLinkedPassengerEmails(request.getPassengerEmails());
        ride.setRoute(route);
        ride.setBabyFriendly(request.isBabyFriendly());
        ride.setPetFriendly(request.isPetFriendly());

        // Assign driver to ride
        if (driver == null) {
            ride.setStatus(RideStatus.FAILED); // later send notification
        }
        else {
            ride.setStatus(RideStatus.SCHEDULED);
            ride.setDriver(driver);
            driver.getScheduledRides().add(ride);
        }

        rideRepository.save(ride);

        // Map na response
        return new RideResponseDTO(
                ride.getId(),
                ride.getStatus(),
                ride.getPrice()
        );
    }

    // Create additional stops
    private List<Location> createStops(List<LocationDTO> incomingList) {
        if (incomingList == null) return null;

        List<Location> list = new ArrayList<>();
        for (LocationDTO loc : incomingList) {
            Location location = new Location();
            location.setLongitude(loc.getLongitude());
            location.setLatitude(loc.getLatitude());
            location.setAddress(loc.getAddress());
            locationRepository.save(location);
            list.add(location);
        }
        return list;
    }

    // Find suitable driver
    private Driver findDriver(List<Driver> drivers) {
        LocalDateTime now = LocalDateTime.now();
        int marginMinutes = 10;

        List<Driver> freeDrivers = new ArrayList<>();
        List<Driver> nearlyFreeDrivers = new ArrayList<>();

        for (Driver d : drivers) {
            // Completely free
            if (d.getScheduledRides().isEmpty()) {
                freeDrivers.add(d);
                continue;
            }

            // Has drives scheduled
            Ride nextRide = d.getScheduledRides().get(0);
            if (nextRide.getStatus() == RideStatus.STARTED) {
                LocalDateTime finishTime = nextRide.getScheduledTime().plusMinutes(nextRide.getRoute().getDuration());

                if (finishTime.isBefore(now.plusMinutes(marginMinutes))) {
                    nearlyFreeDrivers.add(d);
                }
            }
        }

        // Priority have free drivers
        if (!freeDrivers.isEmpty()) {
            return freeDrivers.get(0); // later will be updated based on position
        }

        // Then nearly free drivers
        if (!nearlyFreeDrivers.isEmpty()) {
            return nearlyFreeDrivers.get(0);
        }

        // No available drivers
        return null;
    }

    public void updateDriverStatuses() {
        int marginMinutes = 15;
        driverRepository.updateDriverStatus(8 * 60 + marginMinutes);
    }

    @Override
    @Transactional
    public void cancelAnyRide(Long rideId, RideCancellationRequestDTO request) {

        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride != null) {

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            if (request.getReason() == null || request.getReason().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cancellation reason is required"
                );
            }

            ride.setStatus(RideStatus.CANCELED);
            ride.setCancellationReason(request.getReason());
            ride.setCancelledBy(user);

            rideRepository.save(ride);
            return;
        }

        GuestRide guestRide = guestRideRepository.findById(rideId).orElse(null);
        if (guestRide != null) {

            guestRide.setStatus(RideStatus.CANCELED);
            guestRide.setCancellationReason(request.getReason());

            guestRideRepository.save(guestRide);
            return;
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found");
    }

    @Override
    public void panic(Long rideId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ride not found"
                ));

        if (ride.getStatus() == RideStatus.CANCELED ||
                ride.getStatus() == RideStatus.FINISHED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot trigger panic for finished or canceled ride"
            );
        }

        Panic panic = new Panic();
        panic.setRide(ride);
        panic.setCreatedAt(LocalDateTime.now());

        panicRepository.save(panic);
    }

    public void stopRide(Long rideId, RideStopRequestDTO request) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ride not found"
                ));

        if (ride.getStatus() != RideStatus.STARTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ride is not in progress"
            );
        }

        LocationDTO stop = request.getStopLocation();

        if (ride.getRoute().getDestination().getLatitude() == stop.getLatitude() &&
                ride.getRoute().getDestination().getLongitude() == stop.getLongitude()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Stop location cannot be the same as destination"
            );
        }

        // - price evaluation
        // - destination change
        // - stopping time
        // - status = FINISHED

        Location destination = ride.getRoute().getDestination();

        destination.setLatitude(stop.getLatitude());
        destination.setLongitude(stop.getLongitude());
        destination.setAddress(stop.getAddress());

        ride.setStatus(RideStatus.FINISHED);
        ride.setEndTime(LocalDateTime.now());

        rideRepository.save(ride);
    }

    @Override
    @Transactional
    public void finishRide(Long rideId, String driverEmail) {
        try {

            Ride ride = rideRepository.findById(rideId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));


            if (!ride.getDriver().getEmail().equals(driverEmail)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the driver of this ride.");
            }

            if (ride.getStatus() != RideStatus.STARTED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only started rides can be finished.");
            }

            // Ride update
            ride.setStatus(RideStatus.FINISHED);
            ride.setEndTime(LocalDateTime.now());
            rideRepository.save(ride);

            // Driver update
            Driver driver = ride.getDriver();
            driver.setActiveRide(null);

            // Sending emails
            sendSummaryEmails(ride);
        }
        catch (Exception e) {
            System.out.println("Error in finishRide, sending test email instead: " + e.getMessage());
            sendSummaryEmails("makspavle@gmail.com");
        }



    }

    private void sendSummaryEmails(Ride ride) {
        long minutes = Duration.between(ride.getStartTime(), ride.getEndTime()).toMinutes();
        String routeInfo = ride.getRoute().getOrigin().getAddress() + " -> " +
                ride.getRoute().getDestination().getAddress();

        String subject = "Ride Summary - " + ride.getId();
        String bodyTemplate = """
            Dear Passenger,
            
            Your ride has been successfully finished.
            
            Summary:
            - Route: %s
            - Duration: %d minutes
            - Total Price: %.2f RSD
            
            Thank you for riding with us!
            """;

        String finalBody = String.format(bodyTemplate, routeInfo, minutes, ride.getPrice());

        for (Passenger passenger : ride.getPassengers()) {
            sendEmail(passenger.getEmail(), subject, finalBody);
        }
    }

    // NOVA TESTNA METODA (Overload)
    private void sendSummaryEmails(String testEmail) {
        String subject = "TEST Ride Summary";
        String finalBody = """
            Dear User,
            
            THIS IS A TEST EMAIL (Fallback).
            
            Summary:
            - Route: Test Start -> Test Destination
            - Duration: 15 minutes
            - Total Price: 500.00 RSD
            
            Thank you for testing ClickAndDrive!
            """;

        EmailDetails details = new EmailDetails();
        details.setRecipient(testEmail);
        details.setSubject(subject);
        details.setMsgBody(finalBody);

        emailService.sendsSimpleMail(details);
        System.out.println("Test email sent to: " + testEmail);
    }

    // Pomoćna metoda da ne ponavljaš kod (opciono, ali čisto)
    private void sendEmail(String to, String subject, String body) {
        EmailDetails details = new EmailDetails();
        details.setRecipient(to);
        details.setSubject(subject);
        details.setMsgBody(body);
        emailService.sendsSimpleMail(details);
    }

    @Override
    public InconsistencyReportResponseDTO reportInconsistency(
            Long rideId, InconsistencyReportRequestDTO dto, String passengerEmail) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));

        // started?
        if (ride.getStatus() != RideStatus.STARTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inconsistency can only be reported for active rides.");
        }

        // is passenger on the ride?
        boolean isPassenger = ride.getPassengers().stream()
                .anyMatch(p -> p.getEmail().equals(passengerEmail));
        if (!isPassenger) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a passenger in this ride.");
        }

        // report creation
        InconsistencyReport report = new InconsistencyReport();
        report.setNote(dto.getReason());
        report.setCreatedAt(LocalDateTime.now());
        report.setRide(ride);
        // set Passenger
        Passenger passenger = passengerRepository.findByEmail(passengerEmail).get();
        report.setPassenger(passenger);

        inconsistencyReportRepository.save(report);

        //mapping
        return new InconsistencyReportResponseDTO(
                report.getId(),
                report.getNote(),
                report.getCreatedAt(),
                passenger.getEmail(),
                ride.getId()
        );
    }

    @Override
    public Page<ScheduledRideResponseDTO> getDriverScheduledRides(Long driverId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size); // 0-based page

        // 1️⃣ Učitaj sve SCHEDULED regular rides
        List<Ride> rides = rideRepository.findAllByDriverId(driverId).stream()
                .filter(r -> r.getStatus() == RideStatus.SCHEDULED)
                .toList();

        // 2️⃣ Učitaj sve SCHEDULED guest rides
        List<GuestRide> guestRides = guestRideRepository.findAllByDriverId(driverId).stream()
                .filter(gr -> gr.getStatus() == RideStatus.SCHEDULED)
                .toList();

        // 3️⃣ Mapiranje u DTO i dodavanje flag-a isGuest
        List<ScheduledRideResponseDTO> allRides = new ArrayList<>();
        for (Ride r : rides) {
            allRides.add(new ScheduledRideResponseDTO(
                    r.getId(),
                    r.getRoute().getOrigin().getAddress(),
                    r.getRoute().getDestination().getAddress(),
                    r.getScheduledTime(),
                    false // false = regular ride
            ));
        }

        for (GuestRide gr : guestRides) {
            allRides.add(new ScheduledRideResponseDTO(
                    gr.getId(),
                    gr.getRoute().getOrigin().getAddress(),
                    gr.getRoute().getDestination().getAddress(),
                    gr.getScheduledTime(),
                    true // true = guest ride
            ));
        }

        allRides.sort(Comparator.comparing(ScheduledRideResponseDTO::getScheduledTime));

        int start = Math.min((page - 1) * size, allRides.size());
        int end = Math.min(start + size, allRides.size());
        List<ScheduledRideResponseDTO> pagedList = allRides.subList(start, end);

        return new PageImpl<>(pagedList, pageable, allRides.size());
    }

}
