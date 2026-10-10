package com.example.hospital_management.controller;

import com.example.hospital_management.dto.AppointmentBookRequest;
import com.example.hospital_management.dto.AppointmentResponse;
import com.example.hospital_management.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.hospital_management.dto.AppointmentRescheduleRequest;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> book(
            @Valid @RequestBody AppointmentBookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.book(request));
    }

    @GetMapping("/{id}")
    public AppointmentResponse getById(@PathVariable Long id) {
        return appointmentService.getById(id);
    }
    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable Long id) {
        return appointmentService.cancel(id);
    }

    @PatchMapping("/{id}/reschedule")
    public AppointmentResponse reschedule(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRescheduleRequest request) {
        return appointmentService.reschedule(id, request);
    }
    @PatchMapping("/{id}/complete")
    public AppointmentResponse complete(@PathVariable Long id, @RequestParam Long doctorId) {
        return appointmentService.complete(id, doctorId);
    }
}