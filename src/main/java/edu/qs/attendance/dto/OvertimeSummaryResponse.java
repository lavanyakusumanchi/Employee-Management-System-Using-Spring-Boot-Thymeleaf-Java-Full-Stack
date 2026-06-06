package edu.qs.attendance.dto;

import edu.qs.attendance.entity.SettlementStatus;

import java.math.BigDecimal;
import java.util.List;

public record OvertimeSummaryResponse(
        Long workerId,
        String month,
        double totalOvertimeHours,
        BigDecimal totalPayout,
        SettlementStatus overallStatus,
        List<DayBreakdown> breakdown
) {
    public record DayBreakdown(
            String date,
            double overtimeHours,
            BigDecimal amount,
            SettlementStatus status
    ) {}
}
