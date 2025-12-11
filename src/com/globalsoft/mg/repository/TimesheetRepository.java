package com.jobportal.repository;

import com.jobportal.entity.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
    List<Timesheet> findByStatus(Timesheet.TimesheetStatus status);


    List<Timesheet> findByConsultantUserId(Long consultantId);

    List<Timesheet> findByPlacementPlacementId(Long placementId);

    @Query("SELECT t FROM Timesheet t WHERE t.consultant.userId = :consultantId AND t.weekStartDate = :weekStartDate")
    Optional<Timesheet> findByConsultantAndWeek(@Param("consultantId") Long consultantId,
                                                @Param("weekStartDate") LocalDate weekStartDate);

    @Query("SELECT COUNT(t) FROM Timesheet t WHERE t.status = 'APPROVED'")
    Long countApproved();

    @Query("SELECT t FROM Timesheet t WHERE t.weekStartDate BETWEEN :startDate AND :endDate")
    List<Timesheet> findByDateRange(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);





    @Query("SELECT COUNT(t) FROM Timesheet t WHERE t.status = 'PENDING_REVIEW'")
    Long countPendingReview();

    @Query("SELECT COUNT(t) FROM Timesheet t WHERE t.status = 'SUBMITTED'")
    Long countSubmitted();

    @Query("SELECT COUNT(t) FROM Timesheet t WHERE t.status = :status")
    Long countByStatus(@Param("status") Timesheet.TimesheetStatus status);

    @Query("SELECT SUM(t.totalHours) FROM Timesheet t WHERE t.status = 'APPROVED' AND t.weekStartDate >= :startDate")
    Double sumApprovedHoursSince(@Param("startDate") LocalDate startDate);
}