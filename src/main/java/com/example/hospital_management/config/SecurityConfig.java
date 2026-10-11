package com.example.hospital_management.config;

import com.example.hospital_management.security.JwtAuthenticationFilter;
import com.example.hospital_management.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter =
                new JwtAuthenticationFilter(jwtService, userDetailsService);

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/register/patient", "/api/auth/login").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/doctors").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/doctors", "/api/doctors/*", "/api/doctors/*/slots/available")
                        .authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/doctors/*/slots").hasRole("DOCTOR")

                        .requestMatchers(HttpMethod.POST, "/api/appointments").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/appointments/*/cancel",
                                "/api/appointments/*/reschedule").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/appointments/*/complete").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/*").authenticated()

                        .requestMatchers(HttpMethod.POST,
                                "/api/appointments/*/record").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET,
                                "/api/appointments/*/record").hasRole("PATIENT")

                        .anyRequest().denyAll())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        "Unauthorized",
                                        "Authentication is required. Please log in."))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeError(response, HttpServletResponse.SC_FORBIDDEN,
                                        "Forbidden",
                                        "You do not have permission to do this.")))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    private static void writeError(HttpServletResponse response, int status,
                                   String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                "{\"status\":" + status + ",\"error\":\"" + error
                        + "\",\"message\":\"" + message + "\"}");
    }
}