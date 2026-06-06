package edu.qs.attendance.dto;

import java.math.BigDecimal;

public record SettlementResponse(
        Long workerId,
        String month,
        int entriesSettled,
        BigDecimal totalAmount
) {}
