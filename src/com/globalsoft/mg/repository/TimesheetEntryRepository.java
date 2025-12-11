package com.jobportal.repository;

import com.jobportal.entity.TimesheetEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimesheetEntryRepository extends JpaRepository<TimesheetEntry, Long> {
    List<TimesheetEntry> findByTimesheetTimesheetId(Long timesheetId);

    @Query("SELECT te FROM TimesheetEntry te WHERE te.timesheet.consultant.userId = :consultantId AND te.workDate = :workDate")
    List<TimesheetEntry> findByConsultantAndDate(@Param("consultantId") Long consultantId,
                                                 @Param("workDate") LocalDate workDate);

    @Query("SELECT te FROM TimesheetEntry te WHERE te.timesheet.placement.placementId = :placementId AND te.workDate BETWEEN :startDate AND :endDate")
    List<TimesheetEntry> findByPlacementAndDateRange(@Param("placementId") Long placementId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
}