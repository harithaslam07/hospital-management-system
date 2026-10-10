package com.example.hospital_management.dto;

import jakarta.validation.constraints.NotNull;

public record AppointmentRescheduleRequest(

        @NotNull(message = "New slot id is required")
        Long newSlotId
) {}