package com.example.hospital_management.service;

import com.example.hospital_management.dto.MedicalRecordCreateRequest;
import com.example.hospital_management.dto.MedicalRecordResponse;
import com.example.hospital_management.entity.Appointment;
import com.example.hospital_management.entity.AppointmentStatus;
import com.example.hospital_management.entity.MedicalRecord;
import com.example.hospital_management.exception.ConflictException;
import com.example.hospital_management.exception.DuplicateResourceException;
import com.example.hospital_management.exception.ForbiddenException;
import com.example.hospital_management.exception.ResourceNotFoundException;
import com.example.hospital_management.repository.AppointmentRepository;
import com.example.hospital_management.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public MedicalRecordResponse create(Long appointmentId, Long doctorId,
                                        MedicalRecordCreateRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));

        if (!appointment.getSlot().getDoctor().getId().equals(doctorId)) {
            throw new ForbiddenException("Only the doctor of this appointment can write its record");
        }
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new ConflictException(
                    "A record can be added only after the appointment is COMPLETED");
        }
        if (medicalRecordRepository.existsByAppointmentId(appointmentId)) {
            throw new DuplicateResourceException(
                    "A medical record already exists for this appointment");
        }

        MedicalRecord record = medicalRecordRepository.save(
                MedicalRecord.builder()
                        .appointment(appointment)
                        .diagnosis(request.diagnosis().trim())
                        .prescription(trimOrNull(request.prescription()))
                        .notes(trimOrNull(request.notes()))
                        .build());

        return MedicalRecordResponse.from(record);
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponse getForPatient(Long appointmentId, Long patientId) {
        MedicalRecord record = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> notFound(appointmentId));

        if (!record.getAppointment().getPatient().getId().equals(patientId)) {
            throw notFound(appointmentId);
        }
        return MedicalRecordResponse.from(record);
    }

    private ResourceNotFoundException notFound(Long appointmentId) {
        return new ResourceNotFoundException("Medical record for appointment", appointmentId);
    }

    private String trimOrNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}