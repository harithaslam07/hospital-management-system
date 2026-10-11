package com.example.hospital_management.service;

import com.example.hospital_management.dto.SlotCreateRequest;
import com.example.hospital_management.dto.SlotResponse;
import com.example.hospital_management.entity.Doctor;
import com.example.hospital_management.entity.Slot;
import com.example.hospital_management.exception.BadRequestException;
import com.example.hospital_management.exception.DuplicateResourceException;
import com.example.hospital_management.exception.ResourceNotFoundException;
import com.example.hospital_management.repository.DoctorRepository;
import com.example.hospital_management.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final SlotRepository slotRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public SlotResponse create(Long doctorUserId, SlotCreateRequest request) {
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor profile for user", doctorUserId));

        if (!request.endTime().isAfter(request.startTime())) {
            throw new BadRequestException("End time must be after start time");
        }

        if (slotRepository.existsOverlapping(doctor.getId(), request.startTime(), request.endTime())) {
            throw new DuplicateResourceException(
                    "Doctor already has a slot that overlaps this time");
        }

        Slot slot = slotRepository.save(
                new Slot(doctor, request.startTime(), request.endTime()));
        return SlotResponse.from(slot);
    }

    @Transactional(readOnly = true)
    public List<SlotResponse> getAvailable(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor", doctorId);
        }

        return slotRepository
                .findByDoctorIdAndBookedFalseAndStartTimeAfterOrderByStartTimeAsc(
                        doctorId, LocalDateTime.now())
                .stream()
                .map(SlotResponse::from)
                .toList();
    }
}