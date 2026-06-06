package edu.qs.attendance.dto;

import java.io.Serializable;

// Stored in Redis as the value for each active worker. Kept small + serializable.
public record ActiveWorkerView(
        Long workerId,
        String workerName,
        String designation,
        Long siteId,
        String siteName,
        String clockInTime
) implements Serializable {}
