package edu.qs.attendance.repository;

import edu.qs.attendance.entity.AttendanceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceLog, Long> {

    // Used by clock-out + duplicate clock-in check: find the open (not yet clocked-out) record.
    @Query("SELECT a FROM AttendanceLog a WHERE a.worker.id = :workerId AND a.clockOutTime IS NULL")
    Optional<AttendanceLog> findOpenLogByWorkerId(@Param("workerId") Long workerId);

    boolean existsByWorkerIdAndClockOutTimeIsNull(Long workerId);

    /*
     * TICKET LF-203 FIX:
     * Paginated history with an @EntityGraph so Worker and Site are loaded in the SAME query
     * (LEFT JOIN), eliminating the N+1 problem. Without this, each of N rows triggers 2 extra
     * SELECTs when serialized. Verify with spring.jpa.show-sql=true: you should see ONE select
     * with joins, not one-per-row.
     */
    @EntityGraph(attributePaths = {"worker", "site"})
    @Query("""
            SELECT a FROM AttendanceLog a
            WHERE a.worker.id = :workerId
              AND a.clockInTime >= :from
              AND a.clockInTime < :to
            ORDER BY a.clockInTime DESC
            """)
    Page<AttendanceLog> findHistory(@Param("workerId") Long workerId,
                                    @Param("from") Instant from,
                                    @Param("to") Instant to,
                                    Pageable pageable);
}
