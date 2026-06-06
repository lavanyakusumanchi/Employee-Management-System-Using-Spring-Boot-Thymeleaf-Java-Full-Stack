package edu.qs.attendance.controller;

import edu.qs.attendance.entity.Site;
import edu.qs.attendance.repository.SiteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sites")
public class SiteController {

    private final SiteRepository repo;

    public SiteController(SiteRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<Site> create(@RequestBody Site site) {
        site.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(site));
    }
}
