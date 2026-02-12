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
import java.util.ArrayList;
import java.util.List;
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
			PasswordEncoder passwordEncoder,
			VehiclePriceRepository vehiclePriceRepository) {
		return args -> {


			// ------------------ ADMIN ------------------
			if (administratorRepository.count() == 0) {
				Administrator admin = new Administrator();
				admin.setName("Admin");
				admin.setSurname("Adminovic");
				admin.setEmail("admin@demo.com");
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setAddress("Admin Street 1");
				admin.setPhone("123456789");
				admin.setGender(Gender.MALE);
				administratorRepository.save(admin);
				System.out.println("Admin 'admin@demo.com' created");
			}

			// ------------------ VEHICLES & DRIVERS & LOCATIONS ------------------
			if (driverRepository.count() == 0) {
				double[][] coords = {
						{45.26411740426349, 19.830221442332125},
						{45.242658480042856, 19.882080282032774},
						{45.239920691629045, 19.82537097950451}
				};
				String[] addresses = {
						"Bulevar Kralja Petra I",
						"Futoški put",
						"Bulevar Evrope"
				};

				String[] names = {"Marko", "Ivan", "Petar"};
				String[] surnames = {"Markovic", "Ivanovic", "Petrovic"};
				String[] emails = {"driver@demo.com", "driver2@demo.com", "driver3@demo.com"};
				String[] models = {"Toyota Prius", "Skoda Octavia", "Volkswagen Golf"};
				String[] regs = {"NS-001-AA", "NS-002-BB", "NS-003-CC"};

				for (int i = 0; i < 3; i++) {
					// LOCATIONS CREATION - WITHOUT SAVING!
					Location loc = new Location();
					loc.setLatitude(coords[i][0]);
					loc.setLongitude(coords[i][1]);
					loc.setAddress(addresses[i]);

					// 2. VEHICLE CREATION
					Vehicle v = new Vehicle();
					v.setModel(models[i]);
					v.setRegistration(regs[i]);
					v.setSeats(4);
					v.setType(VehicleType.STANDARD);
					v.setBusy(false);
					v.setIsBabyFriendly(i % 2 == 0);
					v.setIsPetFriendly(true);
					v.setLocation(loc);
					v = vehicleRepository.save(v);

					// 3. DRIVERS CREATION
					Driver d = new Driver();
					d.setName(names[i]);
					d.setSurname(surnames[i]);
					d.setEmail(emails[i]);
					d.setPassword(passwordEncoder.encode("driver123"));
					d.setAddress("Street " + (i + 1));
					d.setPhone("060123456" + i);
					d.setGender(Gender.MALE);
					d.setStatus(DriverStatus.ACTIVE);
					d.setVehicle(v);
					driverRepository.save(d);
				}
				System.out.println("3 Drivers, Vehicles and Locations initialized.");
			}

			// ------------------ PASSENGER ------------------
			Passenger passenger;
			if (passengerRepository.count() == 0) {
				passenger = new Passenger();
				passenger.setName("Jovan");
				passenger.setSurname("Jovanovic");
				passenger.setEmail("passenger@demo.com");
				passenger.setPassword(passwordEncoder.encode("passenger123"));
				passenger.setAddress("Passenger Street 3");
				passenger.setPhone("555666777");
				passenger.setGender(Gender.MALE);
				passenger.setActivated(true);
				passenger.setActivationToken(UUID.randomUUID().toString());
				passenger = passengerRepository.save(passenger);
				System.out.println("Passenger 'passenger@demo.com' created.");
			} else {
				passenger = passengerRepository.findAll().get(0);
			}

			// ---------------- RIDES & ROUTES ----------------
			if (rideRepository.count() == 0) {
				System.out.println("Initializing 3 finished rides for driver history...");

				Driver firstDriver = driverRepository.findAll().get(0);
				Passenger passenger1 = passengerRepository.findAll().get(0);

				//Test adresses
				String[][] addresses = {
						{"Bulevar oslobođenja 45", "Cara Dušana 12"},   // Ride 1
						{"Futoška 10", "Zmaj Jovina 5"},               // Ride 2 (Panic)
						{"Narodnog fronta 22", "Dunavska 1"}           // Ride 3
				};

				double[][] coords = {
						{45.2485, 19.8331, 45.2413, 19.8256}, // Origin(lat, lon), Dest(lat, lon)
						{45.2512, 19.8365, 45.2570, 19.8440},
						{45.2390, 19.8310, 45.2460, 19.8510}
				};

				double[] prices = {750.0, 420.0, 1150.0};

				for (int i = 0; i < 3; i++) {
					// origin - WITH SAVING
					Location locOrigin = new Location();
					locOrigin.setAddress(addresses[i][0]);
					locOrigin.setLatitude(coords[i][0]);
					locOrigin.setLongitude(coords[i][1]);
					locOrigin = locationRepository.save(locOrigin);

					// destination - WITH SAVING
					Location locDest = new Location();
					locDest.setAddress(addresses[i][1]);
					locDest.setLatitude(coords[i][2]);
					locDest.setLongitude(coords[i][3]);
					locDest = locationRepository.save(locDest);

					// route
					Route route = new Route();
					route.setOrigin(locOrigin);
					route.setDestination(locDest);
					route.setDistance(5 + i); // For difference
					route.setDuration(10 + i);
					route = routeRepository.save(route); // If Cascade is turned on...

					// Ride
					Ride ride = new Ride();
					ride.setDriver(firstDriver);
					ride.setRoute(route);
					ride.setPrice(prices[i]);

					// Panic only on second one
					ride.setPanicPressed(i == 1);

					// Time
					ride.setStartTime(LocalDateTime.now().minusDays(i + 1));
					ride.setEndTime(LocalDateTime.now().minusDays(i + 1).plusMinutes(20));
					ride.setStatus(RideStatus.FINISHED);

					// Stopped only on second one
					if(i == 1)
						ride.setStatus(RideStatus.STOPPED);

					// Passenger
					ride.setPassengers(new ArrayList<>(List.of(passenger1)));

					rideRepository.save(ride);
				}

				System.out.println("3 finished rides initialized.");
			}

			// VEHICLE PRICES
			VehiclePrice vehiclePrice = new VehiclePrice();
			vehiclePrice.setPerKm(120.0);
			vehiclePrice.setStandard(200.0);
			vehiclePrice.setVan(400.0);
			vehiclePrice.setLuxury(950.0);

			vehiclePriceRepository.save(vehiclePrice);

		};
	}
}