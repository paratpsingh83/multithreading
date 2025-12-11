//package com.jobportal.dto;
//
//import lombok.Data;
//
//@Data
//public class DashboardOverviewDTO {
//    private Long totalConsultants;
//    private Long totalPlacements;
//    private Long activeThisMonth;
//    private Long pendingInvoices;
//    private Long openJobs;
//    private Long pendingApplications;
//    private Double totalRevenue;
//    private Double revenueThisMonth;
//
//    // Placement types breakdown
//    private Long fullTimePlacements;
//    private Long contractPlacements;
//
//    // Timesheet status counts
//    private Long timesheetPendingReview;
//    private Long timesheetSubmitted;
//    private Long timesheetAwaitingApproval;
//
//    // Invoice status counts
//    private Long invoicePendingReview;
//    private Long invoiceSubmitted;
//    private Long invoiceAwaitingApproval;
//}


package com.jobportal.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class DashboardOverviewDTO {

    @Min(value = 0, message = "Total consultants cannot be negative")
    private Long totalConsultants;

    @Min(value = 0, message = "Total placements cannot be negative")
    private Long totalPlacements;

    @Min(value = 0, message = "Active this month cannot be negative")
    private Long activeThisMonth;

    @Min(value = 0, message = "Pending invoices cannot be negative")
    private Long pendingInvoices;

    @Min(value = 0, message = "Open jobs cannot be negative")
    private Long openJobs;

    @Min(value = 0, message = "Pending applications cannot be negative")
    private Long pendingApplications;

    @Min(value = 0, message = "Total revenue cannot be negative")
    private Double totalRevenue;

    @Min(value = 0, message = "Revenue this month cannot be negative")
    private Double revenueThisMonth;

    // Placement types breakdown
    @Min(value = 0, message = "Full-time placements cannot be negative")
    private Long fullTimePlacements;

    @Min(value = 0, message = "Contract placements cannot be negative")
    private Long contractPlacements;

    // Timesheet status counts
    @Min(value = 0, message = "Timesheet pending review count cannot be negative")
    private Long timesheetPendingReview;

    @Min(value = 0, message = "Timesheet submitted count cannot be negative")
    private Long timesheetSubmitted;

    @Min(value = 0, message = "Timesheet awaiting approval count cannot be negative")
    private Long timesheetAwaitingApproval;

    // Invoice status counts
    @Min(value = 0, message = "Invoice pending review count cannot be negative")
    private Long invoicePendingReview;

    @Min(value = 0, message = "Invoice submitted count cannot be negative")
    private Long invoiceSubmitted;

    @Min(value = 0, message = "Invoice awaiting approval count cannot be negative")
    private Long invoiceAwaitingApproval;

    // NEW FIELD
    @Min(value = 0, message = "New consultants this month cannot be negative")
    private Long newConsultantsThisMonth;
}
