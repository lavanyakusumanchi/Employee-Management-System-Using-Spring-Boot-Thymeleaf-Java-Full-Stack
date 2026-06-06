package edu.qs.attendance.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/*
 * Pure, side-effect-free overtime math (assignment Part 1.4). Kept separate so it can be
 * unit-tested without a database. Rules:
 *   - Standard shift = 8h. Hours beyond 8 in one record = overtime.
 *   - First 2 OT hours: 1.5x the HOURLY wage. Beyond that: 2x.
 *   - Hourly wage is derived from the daily wage assuming an 8-hour standard day.
 *   - Monthly cap = 60 OT hours. Caller supplies hours already used this month; this method
 *     caps the billable OT for the current record so the running total never exceeds 60.
 */
@Component
public class OvertimeCalculator {

    public static final double STANDARD_SHIFT_HOURS = 8.0;
    public static final double TIER1_HOURS = 2.0;       // first 2 OT hours at 1.5x
    public static final double MONTHLY_CAP_HOURS = 60.0;
    public static final double FLAG_THRESHOLD_HOURS = 16.0;

    public record Result(double overtimeHours, BigDecimal amount, BigDecimal rateApplied) {}

    /**
     * @param totalHours      total hours for this single attendance record
     * @param dailyWageRate   worker's daily wage
     * @param otHoursUsedThisMonth overtime hours already recorded this month (before this record)
     */
    public Result calculate(double totalHours, BigDecimal dailyWageRate, double otHoursUsedThisMonth) {
        double rawOvertime = Math.max(0.0, totalHours - STANDARD_SHIFT_HOURS);

        // Apply monthly 60h cap: only bill up to whatever remains under 60.
        double remaining = Math.max(0.0, MONTHLY_CAP_HOURS - otHoursUsedThisMonth);
        double billableOt = Math.min(rawOvertime, remaining);

        if (billableOt <= 0.0) {
            return new Result(0.0, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), BigDecimal.ZERO);
        }

        BigDecimal hourly = dailyWageRate.divide(BigDecimal.valueOf(STANDARD_SHIFT_HOURS), 4, RoundingMode.HALF_UP);

        double tier1 = Math.min(billableOt, TIER1_HOURS);    // up to 2 hours at 1.5x
        double tier2 = Math.max(0.0, billableOt - TIER1_HOURS); // remainder at 2x

        BigDecimal tier1Amount = hourly
                .multiply(BigDecimal.valueOf(1.5))
                .multiply(BigDecimal.valueOf(tier1));
        BigDecimal tier2Amount = hourly
                .multiply(BigDecimal.valueOf(2.0))
                .multiply(BigDecimal.valueOf(tier2));

        BigDecimal amount = tier1Amount.add(tier2Amount).setScale(2, RoundingMode.HALF_UP);

        // "Effective" blended rate stored for traceability (amount / hours).
        BigDecimal rateApplied = amount.divide(BigDecimal.valueOf(billableOt), 2, RoundingMode.HALF_UP);

        return new Result(billableOt, amount, rateApplied);
    }
}
