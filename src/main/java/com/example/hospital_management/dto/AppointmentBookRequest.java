package com.example.hospital_management.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AppointmentBookRequest(

        @NotNull(message = "Patient id is required")
        Long patientId,

        @NotNull(message = "Slot id is required")
        Long slotId,

        @Size(max = 255, message = "Reason must be at most 255 characters")
        String reason
) {}