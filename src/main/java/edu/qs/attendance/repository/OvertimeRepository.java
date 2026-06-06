package edu.qs.attendance.repository;

import edu.qs.attendance.entity.OvertimeEntry;
import edu.qs.attendance.entity.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OvertimeRepository extends JpaRepository<OvertimeEntry, Long> {

    @Query("""
            SELECT o FROM OvertimeEntry o
            WHERE o.worker.id = :workerId
              AND o.date >= :monthStart
              AND o.date < :nextMonthStart
            ORDER BY o.date ASC
            """)
    List<OvertimeEntry> findForWorkerInMonth(@Param("workerId") Long workerId,
                                             @Param("monthStart") LocalDate monthStart,
                                             @Param("nextMonthStart") LocalDate nextMonthStart);

    // Sum of overtime hours already recorded in the month — used to enforce the 60h monthly cap.
    @Query("""
            SELECT COALESCE(SUM(o.overtimeHours), 0)
            FROM OvertimeEntry o
            WHERE o.worker.id = :workerId
              AND o.date >= :monthStart
              AND o.date < :nextMonthStart
            """)
    double sumOvertimeHoursInMonth(@Param("workerId") Long workerId,
                                   @Param("monthStart") LocalDate monthStart,
                                   @Param("nextMonthStart") LocalDate nextMonthStart);

    List<OvertimeEntry> findByWorkerIdAndSettlementStatus(Long workerId, SettlementStatus status);
}
