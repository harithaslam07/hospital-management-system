package com.example.hospital_management.controller;

import com.example.hospital_management.dto.MedicalRecordCreateRequest;
import com.example.hospital_management.dto.MedicalRecordResponse;
import com.example.hospital_management.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments/{appointmentId}/record")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    public ResponseEntity<MedicalRecordResponse> create(
            @PathVariable Long appointmentId,
            @RequestParam Long doctorId,
            @Valid @RequestBody MedicalRecordCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicalRecordService.create(appointmentId, doctorId, request));
    }

    @GetMapping
    public MedicalRecordResponse get(
            @PathVariable Long appointmentId,
            @RequestParam Long patientId) {
        return medicalRecordService.getForPatient(appointmentId, patientId);
    }
}