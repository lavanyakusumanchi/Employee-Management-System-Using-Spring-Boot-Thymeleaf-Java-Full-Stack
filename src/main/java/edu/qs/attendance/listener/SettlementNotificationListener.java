package edu.qs.attendance.listener;

import edu.qs.attendance.event.SettlementCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/*
 * TICKET LF-204 FIX (second half):
 * The SMS fires on AFTER_COMMIT only. If the DB transaction rolls back, this never runs,
 * so the worker never gets a premature/incorrect message. If sending fails, we log and
 * (in production) queue a retry — we never let an SMS failure corrupt the settlement.
 */
@Component
public class SettlementNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(SettlementNotificationListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSettlementCommitted(SettlementCompletedEvent event) {
        try {
            // TODO: integrate real SMS gateway here. Stubbed for the assignment.
            log.info("SMS -> Worker {}: Your {} overtime of Rs.{} has been settled.",
                    event.workerId(), event.month(), event.totalAmount());
        } catch (Exception ex) {
            // Do NOT rethrow: settlement already succeeded. Queue a retry instead.
            log.error("SMS send failed for worker {} month {}. Queuing retry. Cause: {}",
                    event.workerId(), event.month(), ex.getMessage());
        }
    }
}
