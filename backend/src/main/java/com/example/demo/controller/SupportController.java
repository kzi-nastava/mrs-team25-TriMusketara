package com.example.demo.controller;

import com.example.demo.dto.request.ChatMessageRequestDTO;
import com.example.demo.dto.response.ChatMessageResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    //  2.11: Sending msg to support
    @PostMapping("/chat")
    public ResponseEntity<Void> sendMessage(@RequestBody ChatMessageRequestDTO request) {
        return ResponseEntity.ok().build();
    }

    // 2.11: GET chat history
    @GetMapping("/chat")
    public ResponseEntity<List<ChatMessageResponseDTO>> getChatHistory() {
        return ResponseEntity.ok(List.of(
                new ChatMessageResponseDTO(1L, "user@mail.com", "Imam problem sa vožnjom", LocalDateTime.now()),
                new ChatMessageResponseDTO(2L, "admin@uber.com", "Kako možemo da pomognemo?", LocalDateTime.now())
        ));
    }
}