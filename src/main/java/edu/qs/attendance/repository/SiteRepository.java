package edu.qs.attendance.repository;

import edu.qs.attendance.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteRepository extends JpaRepository<Site, Long> {
}
