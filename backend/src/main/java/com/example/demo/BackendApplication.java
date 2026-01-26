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
import java.util.Collections;

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
				vehicle = vehicleRepository.findAll().get(0); // uzmi prvo vozilo ako postoji
			}

			// ------------------ DRIVER ------------------
			if (driverRepository.count() == 0) {
				Driver driver = new Driver();
				driver.setName("Marko");
				driver.setSurname("Markovic");
				driver.setEmail("driver@demo.com");
				driver.setPassword(passwordEncoder.encode("driver123"));
				driver.setAddress("Driver Street 2");
				driver.setPhone("987654321");
				driver.setGender(Gender.MALE);
				driver.setVehicle(vehicle); // koristi managed entity
				driver.setStatus(DriverStatus.ACTIVE);

				driverRepository.save(driver);
				System.out.println("Driver saved with ID: " + driver.getId());
			}

			// ------------------ PASSENGER ------------------
			if (passengerRepository.count() == 0) {
				Passenger passenger = new Passenger();
				passenger.setName("Jovan");
				passenger.setSurname("Jovanovic");
				passenger.setEmail("passenger@demo.com");
				passenger.setPassword(passwordEncoder.encode("passenger123"));
				passenger.setAddress("Passenger Street 3");
				passenger.setPhone("555666777");
				passenger.setGender(Gender.MALE);

				passengerRepository.save(passenger);
				System.out.println("Passenger saved with ID: " + passenger.getId());
			}
		};
	}

}
