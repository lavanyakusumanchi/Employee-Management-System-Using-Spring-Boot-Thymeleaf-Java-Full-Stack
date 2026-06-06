package edu.qs.attendance.dto;

import edu.qs.attendance.entity.AttendanceLog;

import java.time.Instant;

// Response DTO so we never serialize lazy JPA proxies directly (avoids triggering N+1 in Jackson).
public record AttendanceLogView(
        Long id,
        Long workerId,
        String workerName,
        Long siteId,
        String siteName,
        Instant clockInTime,
        Instant clockOutTime,
        Double totalHours,
        Double overtimeHours,
        boolean flagged
) {
    public static AttendanceLogView from(AttendanceLog a) {
        return new AttendanceLogView(
                a.getId(),
                a.getWorker().getId(),
                a.getWorker().getName(),
                a.getSite().getId(),
                a.getSite().getSiteName(),
                a.getClockInTime(),
                a.getClockOutTime(),
                a.getTotalHours(),
                a.getOvertimeHours(),
                a.isFlagged()
        );
    }
}
