package edu.qs.attendance.controller;

import edu.qs.attendance.dto.*;
import edu.qs.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @PostMapping("/clock-in")
    public ResponseEntity<AttendanceLogView> clockIn(@Valid @RequestBody ClockInRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.clockIn(req));
    }

    @PostMapping("/clock-out")
    public ResponseEntity<AttendanceLogView> clockOut(@Valid @RequestBody ClockOutRequest req) {
        return ResponseEntity.ok(service.clockOut(req));
    }

    // Served exclusively from Redis.
    @GetMapping("/active")
    public ResponseEntity<List<ActiveWorkerView>> active() {
        return ResponseEntity.ok(service.activeWorkers());
    }

    // Ticket LF-203: paginated, with sensible defaults (page 0, size 20).
    @GetMapping("/log")
    public ResponseEntity<PagedResponse<AttendanceLogView>> log(
            @RequestParam Long workerId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.history(workerId, from, to, pageable));
    }
}
