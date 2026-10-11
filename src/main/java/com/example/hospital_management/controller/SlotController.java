package com.example.hospital_management.controller;

import com.example.hospital_management.dto.SlotCreateRequest;
import com.example.hospital_management.dto.SlotResponse;
import com.example.hospital_management.security.AppUserDetails;
import com.example.hospital_management.service.SlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @PostMapping("/me/slots")
    public ResponseEntity<SlotResponse> create(
            @AuthenticationPrincipal AppUserDetails principal,
            @Valid @RequestBody SlotCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(slotService.create(principal.getUserId(), request));
    }

    @GetMapping("/{doctorId}/slots/available")
    public List<SlotResponse> getAvailable(@PathVariable Long doctorId) {
        return slotService.getAvailable(doctorId);
    }
}