package com.example.hospital_management.controller;

import com.example.hospital_management.dto.SlotCreateRequest;
import com.example.hospital_management.dto.SlotResponse;
import com.example.hospital_management.service.SlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors/{doctorId}/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @PostMapping
    public ResponseEntity<SlotResponse> create(
            @PathVariable Long doctorId,
            @Valid @RequestBody SlotCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(slotService.create(doctorId, request));
    }

    @GetMapping("/available")
    public List<SlotResponse> getAvailable(@PathVariable Long doctorId) {
        return slotService.getAvailable(doctorId);
    }
}