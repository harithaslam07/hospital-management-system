package com.example.hospital_management.service;

import com.example.hospital_management.dto.PatientRegisterRequest;
import com.example.hospital_management.dto.PatientResponse;
import com.example.hospital_management.entity.Patient;
import com.example.hospital_management.entity.Role;
import com.example.hospital_management.entity.User;
import com.example.hospital_management.exception.DuplicateResourceException;
import com.example.hospital_management.repository.PatientRepository;
import com.example.hospital_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public PatientResponse register(PatientRegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already registered");
        }

        User user = new User(
                request.fullName().trim(),
                email,
                passwordEncoder.encode(request.password()),
                Role.PATIENT
        );
        User savedUser = userRepository.save(user);

        Patient patient = new Patient(savedUser, request.dateOfBirth(), request.phone());
        Patient savedPatient = patientRepository.save(patient);

        return PatientResponse.from(savedPatient);
    }
}