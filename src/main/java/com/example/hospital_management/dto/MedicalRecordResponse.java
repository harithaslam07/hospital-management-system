package com.example.hospital_management.dto;

import com.example.hospital_management.entity.Appointment;
import com.example.hospital_management.entity.MedicalRecord;

import java.time.LocalDateTime;

public record MedicalRecordResponse(
        Long recordId,
        Long appointmentId,
        String patientName,
        String doctorName,
        LocalDateTime visitTime,
        String diagnosis,
        String prescription,
        String notes,
        LocalDateTime createdAt
) {

    public static MedicalRecordResponse from(MedicalRecord record) {
        Appointment appointment = record.getAppointment();
        return new MedicalRecordResponse(
                record.getId(),
                appointment.getId(),
                appointment.getPatient().getUser().getFullName(),
                appointment.getSlot().getDoctor().getUser().getFullName(),
                appointment.getSlot().getStartTime(),
                record.getDiagnosis(),
                record.getPrescription(),
                record.getNotes(),
                record.getCreatedAt()
        );
    }
}