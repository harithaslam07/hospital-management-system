package com.example.hospital_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MedicalRecordCreateRequest(

        @NotBlank(message = "Diagnosis is required")
        @Size(max = 255, message = "Diagnosis must be at most 255 characters")
        String diagnosis,

        @Size(max = 5000, message = "Prescription must be at most 5000 characters")
        String prescription,

        @Size(max = 5000, message = "Notes must be at most 5000 characters")
        String notes
) {}