package com.example.hospital_management.dto;

import com.example.hospital_management.entity.Doctor;

public record DoctorResponse(
        Long doctorId,
        String fullName,
        String email,
        String specialization,
        Integer experienceYears
) {

    public static DoctorResponse from(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getUser().getFullName(),
                doctor.getUser().getEmail(),
                doctor.getSpecialization(),
                doctor.getExperienceYears()
        );
    }
}