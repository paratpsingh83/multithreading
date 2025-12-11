package com.jobportal.dto;

import lombok.Data;

@Data
public class DashboardSummaryDTO {
    private DashboardOverviewDTO overview;
    private PlacementStatsDTO placementStats;
    private TimesheetStatsDTO timesheetStats;
    private InvoiceStatsDTO invoiceStats;
    private ConsultantStatsDTO consultantStats;
    private RevenueMetricsDTO revenueMetrics;
    private PerformanceMetricsDTO performanceMetrics;
}