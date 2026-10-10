package com.example.hospital_management.dto;

import com.example.hospital_management.entity.Patient;
import com.example.hospital_management.entity.Role;

import java.time.LocalDate;

public record PatientResponse(
        Long patientId,
        Long userId,
        String fullName,
        String email,
        Role role,
        LocalDate dateOfBirth,
        String phone
) {

    public static PatientResponse from(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getUser().getId(),
                patient.getUser().getFullName(),
                patient.getUser().getEmail(),
                patient.getUser().getRole(),
                patient.getDateOfBirth(),
                patient.getPhone()
        );
    }
}
