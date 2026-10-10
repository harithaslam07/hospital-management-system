package com.example.hospital_management.dto;

import com.example.hospital_management.entity.Slot;

import java.time.LocalDateTime;

public record SlotResponse(
        Long slotId,
        Long doctorId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        boolean booked
) {

    public static SlotResponse from(Slot slot) {
        return new SlotResponse(
                slot.getId(),
                slot.getDoctor().getId(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.isBooked()
        );
    }
}