package edu.qs.attendance.exception;

import java.time.Instant;

// Matches the exact format required by the assignment.
public record ErrorResponse(String error, String message, Instant timestamp) {
    public static ErrorResponse of(String error, String message) {
        return new ErrorResponse(error, message, Instant.now());
    }
}
