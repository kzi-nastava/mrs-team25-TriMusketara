package com.example.demo.controller;

import com.example.demo.dto.request.ReportRequestDTO;
import com.example.demo.dto.response.ReportResponseDTO;
import com.example.demo.model.User;
import com.example.demo.services.ReportServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportController {

    @Autowired
    private ReportServiceImpl reportService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('DRIVER', 'USER', 'ADMIN')")
    public ResponseEntity<ReportResponseDTO> generateReport(@RequestBody ReportRequestDTO request, Authentication authentication) {
        String email;
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            email = ((User) principal).getEmail();
        } else if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        // Get role
        String rawRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        String role = rawRole.startsWith("ROLE_") ? rawRole.substring(5) : rawRole;
        role = role.toUpperCase();

        if ("USER".equals(role)) {
            role = "PASSENGER";
        }
        ReportResponseDTO report = reportService.generateReport(request, email, role);
        return ResponseEntity.ok(report);
    }
}
