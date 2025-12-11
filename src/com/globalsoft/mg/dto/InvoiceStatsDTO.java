package com.jobportal.dto;

import lombok.Data;
import java.util.Map;

@Data
public class InvoiceStatsDTO {
    private Map<String, Long> statusCounts; // DRAFT, PENDING_REVIEW, SUBMITTED, AWAITING_APPROVAL, PAID, OVERDUE
    private Long totalInvoices;
    private Long pendingReviewCount;
    private Long submittedCount;
    private Long awaitingApprovalCount;
    private Double totalOutstanding;
    private Double totalPaidThisMonth;
}