package com.example.hospital_management.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {

    public static ErrorResponse of(HttpStatus status, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), message, path, null);
    }

    public static ErrorResponse of(HttpStatus status, String message, String path,
                                   Map<String, String> fieldErrors) {
        return new ErrorResponse(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), message, path, fieldErrors);
    }
}
