package edu.qs.attendance.controller;

import edu.qs.attendance.entity.Worker;
import edu.qs.attendance.service.WorkerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    private final WorkerService service;

    public WorkerController(WorkerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Worker> create(@RequestBody Worker worker) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(worker));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Worker> update(@PathVariable Long id, @RequestBody Worker worker) {
        return ResponseEntity.ok(service.update(id, worker));
    }
}
