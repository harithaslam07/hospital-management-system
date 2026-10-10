package com.example.hospital_management.service;

import com.example.hospital_management.dto.DoctorCreateRequest;
import com.example.hospital_management.dto.DoctorResponse;
import com.example.hospital_management.entity.Doctor;
import com.example.hospital_management.entity.Role;
import com.example.hospital_management.entity.User;
import com.example.hospital_management.exception.DuplicateResourceException;
import com.example.hospital_management.exception.ResourceNotFoundException;
import com.example.hospital_management.repository.DoctorRepository;
import com.example.hospital_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DoctorResponse create(DoctorCreateRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already registered");
        }

        User user = userRepository.save(new User(
                request.fullName().trim(),
                email,
                passwordEncoder.encode(request.password()),
                Role.DOCTOR
        ));

        Doctor doctor = doctorRepository.save(new Doctor(
                user,
                request.specialization().trim(),
                request.experienceYears()
        ));

        return DoctorResponse.from(doctor);
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> getAll() {
        return doctorRepository.findAllWithUser().stream()
                .map(DoctorResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DoctorResponse getById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        return DoctorResponse.from(doctor);
    }
}