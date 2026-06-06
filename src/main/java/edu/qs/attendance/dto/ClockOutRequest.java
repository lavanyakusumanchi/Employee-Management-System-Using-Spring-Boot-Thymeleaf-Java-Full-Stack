package edu.qs.attendance.dto;

import jakarta.validation.constraints.NotNull;

public record ClockOutRequest(
        @NotNull(message = "workerId is required") Long workerId
) {}
