package com.jobportal.dto;

import lombok.Data;
import java.util.Map;

@Data
public class TimesheetStatsDTO {
    private Map<String, Long> statusCounts; // DRAFT, SUBMITTED, PENDING_REVIEW, APPROVED, REJECTED
    private Long totalTimesheets;
    private Long pendingReviewCount;
    private Long submittedCount;
    private Long awaitingApprovalCount;
    private Double totalHoursThisMonth;
}