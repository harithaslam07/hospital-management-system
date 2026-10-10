package com.example.hospital_management.service;
import com.example.hospital_management.dto.AppointmentRescheduleRequest;
import com.example.hospital_management.entity.AppointmentStatus;
import com.example.hospital_management.dto.AppointmentBookRequest;
import com.example.hospital_management.dto.AppointmentResponse;
import com.example.hospital_management.entity.Appointment;
import com.example.hospital_management.entity.Patient;
import com.example.hospital_management.entity.Slot;
import com.example.hospital_management.exception.BadRequestException;
import com.example.hospital_management.exception.ConflictException;
import com.example.hospital_management.exception.ResourceNotFoundException;
import com.example.hospital_management.repository.AppointmentRepository;
import com.example.hospital_management.repository.PatientRepository;
import com.example.hospital_management.repository.SlotRepository;
import com.example.hospital_management.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final SlotRepository slotRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public AppointmentResponse book(AppointmentBookRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", request.patientId()));

        Slot slot = slotRepository.findByIdForUpdate(request.slotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot", request.slotId()));

        if (slot.isBooked()) {
            throw new ConflictException("Slot is already booked");
        }
        if (slot.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot book a slot that has already started");
        }

        slot.setBooked(true);

        String reason = request.reason() == null ? null : request.reason().trim();
        Appointment appointment = appointmentRepository.save(
                Appointment.builder()
                        .patient(patient)
                        .slot(slot)
                        .reason(reason)
                        .build());

        return AppointmentResponse.from(appointment);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
        return AppointmentResponse.from(appointment);
    }
    @Transactional
    public AppointmentResponse cancel(Long appointmentId) {
        Appointment appointment = lockAppointment(appointmentId);

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new ConflictException(
                    "Only BOOKED appointments can be cancelled. Current status: "
                            + appointment.getStatus());
        }

        Slot slot = lockSlot(appointment.getSlot().getId());
        if (slot.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot cancel an appointment that has already started");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        slot.setBooked(false);

        return AppointmentResponse.from(appointment);
    }

    @Transactional

    public AppointmentResponse reschedule(Long appointmentId, AppointmentRescheduleRequest request) {
        Appointment appointment = lockAppointment(appointmentId);

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new ConflictException(
                    "Only BOOKED appointments can be rescheduled. Current status: "
                            + appointment.getStatus());
        }

        Long oldSlotId = appointment.getSlot().getId();
        Long newSlotId = request.newSlotId();

        if (oldSlotId.equals(newSlotId)) {
            throw new BadRequestException("New slot must be different from the current slot");
        }

        // Always lock in ascending id order, so two reschedules can never wait on each other
        Slot lockedFirst = lockSlot(Math.min(oldSlotId, newSlotId));
        Slot lockedSecond = lockSlot(Math.max(oldSlotId, newSlotId));
        Slot oldSlot = lockedFirst.getId().equals(oldSlotId) ? lockedFirst : lockedSecond;
        Slot newSlot = lockedFirst.getId().equals(newSlotId) ? lockedFirst : lockedSecond;

        if (oldSlot.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot reschedule an appointment that has already started");
        }
        if (!oldSlot.getDoctor().getId().equals(newSlot.getDoctor().getId())) {
            throw new BadRequestException("New slot must belong to the same doctor");
        }
        if (newSlot.isBooked()) {
            throw new ConflictException("The new slot is already booked");
        }
        if (newSlot.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot move to a slot that has already started");
        }

        oldSlot.setBooked(false);
        newSlot.setBooked(true);
        appointment.setSlot(newSlot);

        return AppointmentResponse.from(appointment);
    }

    private Appointment lockAppointment(Long id) {
        return appointmentRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }

    private Slot lockSlot(Long id) {
        return slotRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", id));
    }
    @Transactional
    public AppointmentResponse complete(Long appointmentId, Long doctorId) {
        Appointment appointment = lockAppointment(appointmentId);

        if (!appointment.getSlot().getDoctor().getId().equals(doctorId)) {
            throw new ForbiddenException("Only the doctor of this appointment can complete it");
        }
        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new ConflictException(
                    "Only BOOKED appointments can be completed. Current status: "
                            + appointment.getStatus());
        }
        if (appointment.getSlot().getStartTime().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Cannot complete an appointment before it starts");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        return AppointmentResponse.from(appointment);
    }

}