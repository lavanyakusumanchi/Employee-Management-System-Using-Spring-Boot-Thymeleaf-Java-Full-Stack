package edu.qs.attendance.service;

import edu.qs.attendance.dto.*;
import edu.qs.attendance.entity.*;
import edu.qs.attendance.exception.ApiException;
import edu.qs.attendance.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Service
public class AttendanceService {

    private final WorkerRepository workerRepo;
    private final SiteRepository siteRepo;
    private final AttendanceRepository attendanceRepo;
    private final OvertimeRepository overtimeRepo;
    private final ActiveWorkerCacheService activeCache;
    private final OvertimeCalculator calculator;

    public AttendanceService(WorkerRepository workerRepo, SiteRepository siteRepo,
                             AttendanceRepository attendanceRepo, OvertimeRepository overtimeRepo,
                             ActiveWorkerCacheService activeCache, OvertimeCalculator calculator) {
        this.workerRepo = workerRepo;
        this.siteRepo = siteRepo;
        this.attendanceRepo = attendanceRepo;
        this.overtimeRepo = overtimeRepo;
        this.activeCache = activeCache;
        this.calculator = calculator;
    }

    @Transactional
    public AttendanceLogView clockIn(ClockInRequest req) {
        Worker worker = workerRepo.findById(req.workerId())
                .orElseThrow(() -> ApiException.notFound("WORKER_NOT_FOUND",
                        "No worker with id " + req.workerId()));
        if (!worker.isActive()) {
            throw ApiException.badRequest("WORKER_INACTIVE", "Worker is not active");
        }
        Site site = siteRepo.findById(req.siteId())
                .orElseThrow(() -> ApiException.notFound("SITE_NOT_FOUND",
                        "No site with id " + req.siteId()));
        if (!site.isActive()) {
            throw ApiException.badRequest("SITE_INACTIVE", "Site is not active");
        }
        // No double clock-in.
        if (attendanceRepo.existsByWorkerIdAndClockOutTimeIsNull(worker.getId())) {
            throw ApiException.conflict("DUPLICATE_CLOCK_IN",
                    "Worker is already clocked in");
        }

        Instant now = Instant.now();
        AttendanceLog log = AttendanceLog.builder()
                .worker(worker)
                .site(site)
                .clockInTime(now)
                .flagged(false)
                .build();
        log = attendanceRepo.save(log);

        // Add to Redis active set with TTL.
        activeCache.addActive(new ActiveWorkerView(
                worker.getId(), worker.getName(), worker.getDesignation().name(),
                site.getId(), site.getSiteName(), now.toString()));

        return AttendanceLogView.from(log);
    }

    @Transactional
    public AttendanceLogView clockOut(ClockOutRequest req) {
        AttendanceLog log = attendanceRepo.findOpenLogByWorkerId(req.workerId())
                .orElseThrow(() -> ApiException.conflict("NOT_CLOCKED_IN",
                        "Worker is not currently clocked in"));

        Instant now = Instant.now();
        double totalHours = Duration.between(log.getClockInTime(), now).toMinutes() / 60.0;
        totalHours = Math.round(totalHours * 100.0) / 100.0;

        log.setClockOutTime(now);
        log.setTotalHours(totalHours);

        // Flag shifts over 16 hours (likely missed clock-out).
        if (totalHours > OvertimeCalculator.FLAG_THRESHOLD_HOURS) {
            log.setFlagged(true);
        }

        // Overtime calc, respecting the monthly 60h cap.
        Worker worker = log.getWorker();
        LocalDate today = LocalDate.ofInstant(now, ZoneOffset.UTC);
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate nextMonthStart = monthStart.plusMonths(1);
        double otUsed = overtimeRepo.sumOvertimeHoursInMonth(worker.getId(), monthStart, nextMonthStart);

        OvertimeCalculator.Result result =
                calculator.calculate(totalHours, worker.getDailyWageRate(), otUsed);

        log.setOvertimeHours(result.overtimeHours());
        attendanceRepo.save(log);

        if (result.overtimeHours() > 0) {
            OvertimeEntry entry = OvertimeEntry.builder()
                    .worker(worker)
                    .attendance(log)
                    .date(today)
                    .overtimeHours(result.overtimeHours())
                    .overtimeRateApplied(result.rateApplied())
                    .amount(result.amount())
                    .settlementStatus(SettlementStatus.PENDING)
                    .build();
            overtimeRepo.save(entry);
        }

        // Remove from Redis active set.
        activeCache.removeActive(worker.getId());

        return AttendanceLogView.from(log);
    }

    // Reads EXCLUSIVELY from Redis (assignment requirement).
    public java.util.List<ActiveWorkerView> activeWorkers() {
        return activeCache.listActive();
    }

    @Transactional(readOnly = true)
    public PagedResponse<AttendanceLogView> history(Long workerId, LocalDate from, LocalDate to,
                                                    Pageable pageable) {
        Instant fromI = from.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant toI = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant(); // inclusive end day
        Page<AttendanceLog> page = attendanceRepo.findHistory(workerId, fromI, toI, pageable);
        return PagedResponse.from(page, AttendanceLogView::from);
    }
}
