package com.example.demo.services.interfaces;

import com.example.demo.dto.response.PanicResponseDTO;

import java.util.List;

public interface PanicService {
    List<PanicResponseDTO> getAll();
}
