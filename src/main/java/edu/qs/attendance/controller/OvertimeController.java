package edu.qs.attendance.controller;

import edu.qs.attendance.dto.OvertimeSummaryResponse;
import edu.qs.attendance.dto.SettlementResponse;
import edu.qs.attendance.service.OvertimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/overtime")
public class OvertimeController {

    private final OvertimeService service;

    public OvertimeController(OvertimeService service) {
        this.service = service;
    }

    @GetMapping("/summary/{workerId}")
    public ResponseEntity<OvertimeSummaryResponse> summary(
            @PathVariable Long workerId,
            @RequestParam String month) {
        return ResponseEntity.ok(service.summary(workerId, month));
    }

    @PostMapping("/settle/{workerId}")
    public ResponseEntity<SettlementResponse> settle(
            @PathVariable Long workerId,
            @RequestParam String month) {
        return ResponseEntity.ok(service.settle(workerId, month));
    }
}
