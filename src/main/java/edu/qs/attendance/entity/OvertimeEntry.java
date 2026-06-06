package edu.qs.attendance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "overtime_entries",
    indexes = {
        // Supports monthly summary/settlement queries (worker + date range).
        @Index(name = "idx_overtime_worker_date", columnList = "worker_id, entry_date"),
        @Index(name = "idx_overtime_status", columnList = "settlement_status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OvertimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    // One overtime entry corresponds to one attendance record.
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attendance_id", nullable = false, unique = true)
    private AttendanceLog attendance;

    @Column(name = "entry_date", nullable = false)
    private LocalDate date;

    @Column(name = "overtime_hours", nullable = false)
    private Double overtimeHours;

    @Column(name = "overtime_rate_applied", nullable = false, precision = 10, scale = 2)
    private BigDecimal overtimeRateApplied;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "settlement_status", nullable = false, length = 12)
    private SettlementStatus settlementStatus = SettlementStatus.PENDING;
}
