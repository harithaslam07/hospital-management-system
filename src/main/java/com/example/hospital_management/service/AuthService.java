package com.example.hospital_management.service;

import com.example.hospital_management.dto.LoginRequest;
import com.example.hospital_management.dto.LoginResponse;
import com.example.hospital_management.security.AppUserDetails;
import com.example.hospital_management.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));

        AppUserDetails user = (AppUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        return new LoginResponse(token, "Bearer", user.getRole());
    }
}