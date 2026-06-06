package edu.qs.attendance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
    name = "attendance_logs",
    indexes = {
        // Composite index supports the common query: history for a worker within a date range.
        @Index(name = "idx_attendance_worker_clockin", columnList = "worker_id, clock_in_time"),
        @Index(name = "idx_attendance_site", columnList = "site_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LAZY on purpose. EAGER would load Worker on every query and cause N+1 (ticket LF-203).
    // We resolve N+1 explicitly with JOIN FETCH / @EntityGraph in the repository.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "clock_in_time", nullable = false)
    private Instant clockInTime;

    @Column(name = "clock_out_time")
    private Instant clockOutTime;

    // Total hours worked, computed at clock-out.
    @Column(name = "total_hours")
    private Double totalHours;

    // Overtime portion of total hours (after the 8h standard shift), capped by the 60h monthly rule.
    @Column(name = "overtime_hours")
    private Double overtimeHours;

    // Auto-flagged when a shift exceeds 16 hours (likely a missed clock-out).
    @Column(nullable = false)
    private boolean flagged = false;
}
