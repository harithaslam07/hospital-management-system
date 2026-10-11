package com.example.hospital_management.config;

import com.example.hospital_management.entity.Role;
import com.example.hospital_management.entity.User;
import com.example.hospital_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (adminPassword == null || adminPassword.length() < 8) {
            log.warn("ADMIN_PASSWORD is missing or shorter than 8 characters. "
                    + "No admin account was created.");
            return;
        }

        String email = adminEmail.trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            log.info("Admin account already exists: {}", email);
            return;
        }

        userRepository.save(new User(
                "Administrator",
                email,
                passwordEncoder.encode(adminPassword),
                Role.ADMIN));
        log.info("Admin account created: {}", email);
    }
}