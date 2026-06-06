package edu.qs.attendance.event;

import java.math.BigDecimal;

/*
 * TICKET LF-204:
 * Published AFTER the settlement transaction commits. The SMS listener reacts to this event
 * only on a successful commit, so a rolled-back settlement never sends a message.
 */
public record SettlementCompletedEvent(
        Long workerId,
        String month,
        BigDecimal totalAmount
) {}
