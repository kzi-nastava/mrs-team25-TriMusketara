package com.example.demo;

import com.example.demo.model.*;
import com.example.demo.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootApplication
public class BackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	@Transactional
	public CommandLineRunner initData(
			AdministratorRepository administratorRepository,
			DriverRepository driverRepository,
			PassengerRepository passengerRepository,
			VehicleRepository vehicleRepository,
			RideRepository rideRepository,
			LocationRepository locationRepository,
			RouteRepository routeRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {

			// ------------------ ADMIN ------------------
			if (administratorRepository.count() == 0) {
				Chat chat = new Chat();
				Administrator admin = new Administrator();
				admin.setName("Admin");
				admin.setSurname("Adminovic");
				admin.setEmail("admin@demo.com");
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setAddress("Admin Street 1");
				admin.setPhone("123456789");
				admin.setGender(Gender.MALE);
				admin.setChat(chat);
				administratorRepository.save(admin);
				System.out.println("Admin saved with ID: " + admin.getId());
			}

			// ------------------ VEHICLE ------------------
			Vehicle vehicle;
			if (vehicleRepository.count() == 0) {
				vehicle = new Vehicle();
				vehicle.setModel("Toyota Prius");
				vehicle.setRegistration("NS123AB");
				vehicle.setSeats(4);
				vehicle.setType(VehicleType.STANDARD);
				vehicle.setBusy(false);
				vehicle.setIsBabyFriendly(true);
				vehicle.setIsPetFriendly(false);
				vehicle = vehicleRepository.save(vehicle);
				System.out.println("Vehicle saved with ID: " + vehicle.getId());
			} else {
				vehicle = vehicleRepository.findAll().get(0);
			}

			// ------------------ DRIVER ------------------
			Driver driver;
			if (driverRepository.count() == 0) {
				driver = new Driver();
				driver.setName("Marko");
				driver.setSurname("Markovic");
				driver.setEmail("driver@demo.com");
				driver.setPassword(passwordEncoder.encode("driver123"));
				driver.setAddress("Driver Street 2");
				driver.setPhone("987654321");
				driver.setGender(Gender.MALE);
				driver.setVehicle(vehicle);
				driver.setStatus(DriverStatus.ACTIVE);
				driverRepository.save(driver);
				System.out.println("Driver saved with ID: " + driver.getId());
			} else {
				driver = driverRepository.findAll().get(0);
			}

			// ------------------ PASSENGER ------------------
			/*if (passengerRepository.count() == 0) {
				Passenger passenger = new Passenger();
				passenger.setName("Jovan");
				passenger.setSurname("Jovanovic");
				passenger.setEmail("passenger@demo.com");
				passenger.setPassword(passwordEncoder.encode("passenger123"));
				passenger.setAddress("Passenger Street 3");
				passenger.setPhone("555666777");
				passenger.setGender(Gender.MALE);
				passenger.setActivated(false);
				passenger.setActivationToken(UUID.randomUUID().toString());
				passengerRepository.save(passenger);
				System.out.println("Passenger saved with ID: " + passenger.getId());
			}*/

			// ---------------- RIDES, ROUTES & LOCATIONS ----------------



			if (rideRepository.count() == 0) {
				System.out.println("Initializing test rides...");

				// ORIGIN
				Location loc1 = new Location();
				loc1.setAddress("Bulevar oslobođenja 45");
				loc1.setLatitude(45.2485);
				loc1.setLongitude(19.8331);
				locationRepository.save(loc1);

				// DESTINATION
				Location loc2 = new Location();
				loc2.setAddress("Cara Dušana 12");
				loc2.setLatitude(45.2413);
				loc2.setLongitude(19.8256);
				locationRepository.save(loc2);

				// ROUTES
				Route route1 = new Route();
				route1.setOrigin(loc1);
				route1.setDestination(loc2);
				routeRepository.save(route1);

				Route route2 = new Route();
				route2.setOrigin(loc2);
				route2.setDestination(loc1);
				routeRepository.save(route2);

				// RIDES
				Ride ride1 = new Ride();
				ride1.setDriver(driver);
				ride1.setRoute(route1);
				ride1.setPrice(758.0);
				ride1.setPanicPressed(false);
				ride1.setStartTime(LocalDateTime.now().minusDays(1));
				ride1.setStatus(RideStatus.FINISHED);
				rideRepository.save(ride1);

				Ride ride2 = new Ride();
				ride2.setDriver(driver);
				ride2.setRoute(route2);
				ride2.setPrice(920.0);
				ride2.setPanicPressed(true);
				ride2.setStartTime(LocalDateTime.now().minusDays(2));
				ride2.setStatus(RideStatus.FINISHED);
				rideRepository.save(ride2);

				System.out.println("Test rides initialized.");
			} else {
				System.out.println("Rides already exist, skipping initialization.");
			}
		};
	}
}