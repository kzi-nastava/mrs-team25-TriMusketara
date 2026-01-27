package com.example.demo.services;

import com.example.demo.dto.response.PanicResponseDTO;
import com.example.demo.model.Panic;
import com.example.demo.repositories.PanicRepository;
import com.example.demo.services.interfaces.PanicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PanicServiceImpl implements PanicService {
    private final PanicRepository panicRepository;

    @Override
    public List<PanicResponseDTO> getAll() {

        List<Panic> panics = panicRepository.findAllByOrderByCreatedAtDesc();

        if (panics.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NO_CONTENT,
                    "No panic notifications found"
            );
        }

        return panics.stream()
                .map(p -> new PanicResponseDTO(
                        p.getId(),
                        p.getRide().getId(),
                        p.getCreatedAt()
                ))
                .toList();
    }
}
