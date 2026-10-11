package com.example.hospital_management.dto;

import com.example.hospital_management.entity.Role;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Role role
) {}