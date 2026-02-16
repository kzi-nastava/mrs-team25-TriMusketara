package com.example.demo.services;

import com.example.demo.dto.request.ReportRequestDTO;
import com.example.demo.dto.response.ReportResponseDTO;
import com.example.demo.repositories.DriverRepository;
import com.example.demo.repositories.PassengerRepository;
import com.example.demo.repositories.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl {

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;

    public ReportResponseDTO generateReport(ReportRequestDTO request, String currentUserEmail, String currentUserRole) {

        // Validate date
        if (request.getDateFrom().isAfter(request.getDateTo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date FROM cannot be after date TO");
        }


    }

}
