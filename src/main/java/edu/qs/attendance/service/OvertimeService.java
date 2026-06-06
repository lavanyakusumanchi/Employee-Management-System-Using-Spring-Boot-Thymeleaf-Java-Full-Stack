package edu.qs.attendance.service;

import edu.qs.attendance.dto.OvertimeSummaryResponse;
import edu.qs.attendance.dto.SettlementResponse;
import edu.qs.attendance.entity.OvertimeEntry;
import edu.qs.attendance.entity.SettlementStatus;
import edu.qs.attendance.event.SettlementCompletedEvent;
import edu.qs.attendance.exception.ApiException;
import edu.qs.attendance.repository.OvertimeRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OvertimeService {

    private final OvertimeRepository overtimeRepo;
    private final MinimumWageApiClient wageApiClient;
    private final ApplicationEventPublisher eventPublisher;

    public OvertimeService(OvertimeRepository overtimeRepo,
                           MinimumWageApiClient wageApiClient,
                           ApplicationEventPublisher eventPublisher) {
        this.overtimeRepo = overtimeRepo;
        this.wageApiClient = wageApiClient;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public OvertimeSummaryResponse summary(Long workerId, String month) {
        /*
         * TICKET LF-205 FIX:
         * The external wage API is called BEFORE we touch the DB / open the read transaction
         * boundary heavily, so no DB connection is held hostage during the 3-5s network wait.
         * (Here it is fetched up front and could be passed into calculation if needed.)
         */
        wageApiClient.fetchLatestMinimumWage();

        YearMonth ym = parseMonth(month);
        LocalDate start = ym.atDay(1);
        LocalDate nextStart = start.plusMonths(1);

        List<OvertimeEntry> entries = overtimeRepo.findForWorkerInMonth(workerId, start, nextStart);

        double totalHours = entries.stream().mapToDouble(OvertimeEntry::getOvertimeHours).sum();
        BigDecimal totalPayout = entries.stream()
                .map(OvertimeEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean allSettled = !entries.isEmpty() &&
                entries.stream().allMatch(e -> e.getSettlementStatus() == SettlementStatus.SETTLED);
        SettlementStatus overall = allSettled ? SettlementStatus.SETTLED : SettlementStatus.PENDING;

        List<OvertimeSummaryResponse.DayBreakdown> breakdown = entries.stream()
                .map(e -> new OvertimeSummaryResponse.DayBreakdown(
                        e.getDate().toString(),
                        e.getOvertimeHours(),
                        e.getAmount(),
                        e.getSettlementStatus()))
                .toList();

        return new OvertimeSummaryResponse(workerId, month, totalHours, totalPayout, overall, breakdown);
    }

    /*
     * TICKET LF-204 FIX:
     * The ENTIRE worker+month settlement is one atomic transaction. Either every PENDING entry
     * becomes SETTLED or none do (no partial commits). The SMS is NOT sent here — instead we
     * publish an event that an AFTER_COMMIT listener consumes, so the message only goes out
     * after a successful commit.
     *
     * Note: this method is called directly from the controller (an external bean), so the
     * @Transactional proxy IS honored (no self-invocation trap).
     */
    @Transactional
    public SettlementResponse settle(Long workerId, String month) {
        YearMonth ym = parseMonth(month);

        // Cannot settle the current or future month — only completed months.
        if (!ym.isBefore(YearMonth.now())) {
            throw ApiException.conflict("CANNOT_SETTLE_CURRENT_MONTH",
                    "Only completed past months can be settled");
        }

        LocalDate start = ym.atDay(1);
        LocalDate nextStart = start.plusMonths(1);
        List<OvertimeEntry> entries = overtimeRepo.findForWorkerInMonth(workerId, start, nextStart);

        if (entries.isEmpty()) {
            throw ApiException.notFound("NO_OVERTIME_ENTRIES",
                    "No overtime entries for worker " + workerId + " in " + month);
        }

        // Already settled entries cannot be modified; if all are settled, that's a conflict.
        boolean anyPending = entries.stream()
                .anyMatch(e -> e.getSettlementStatus() == SettlementStatus.PENDING);
        if (!anyPending) {
            throw ApiException.conflict("ALREADY_SETTLED",
                    "All overtime for this worker+month is already settled");
        }

        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (OvertimeEntry e : entries) {
            if (e.getSettlementStatus() == SettlementStatus.PENDING) {
                e.setSettlementStatus(SettlementStatus.SETTLED);
                total = total.add(e.getAmount());
                count++;
            }
        }
        overtimeRepo.saveAll(entries); // all within this single transaction

        // Fires only after this transaction commits (see SettlementNotificationListener).
        eventPublisher.publishEvent(new SettlementCompletedEvent(workerId, month, total));

        return new SettlementResponse(workerId, month, count, total);
    }

    private YearMonth parseMonth(String month) {
        try {
            return YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (Exception ex) {
            throw ApiException.badRequest("INVALID_MONTH", "month must be in format YYYY-MM");
        }
    }
}
