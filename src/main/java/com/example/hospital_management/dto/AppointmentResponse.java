package com.example.hospital_management.dto;

import com.example.hospital_management.entity.Appointment;
import com.example.hospital_management.entity.AppointmentStatus;
import com.example.hospital_management.entity.Slot;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long appointmentId,
        Long patientId,
        String patientName,
        Long slotId,
        Long doctorId,
        String doctorName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status,
        String reason,
        LocalDateTime createdAt
) {

    public static AppointmentResponse from(Appointment appointment) {
        Slot slot = appointment.getSlot();
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getUser().getFullName(),
                slot.getId(),
                slot.getDoctor().getId(),
                slot.getDoctor().getUser().getFullName(),
                slot.getStartTime(),
                slot.getEndTime(),
                appointment.getStatus(),
                appointment.getReason(),
                appointment.getCreatedAt()
        );
    }
}