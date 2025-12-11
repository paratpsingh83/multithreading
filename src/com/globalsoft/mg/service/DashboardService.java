package com.jobportal.service;

import com.jobportal.dto.*;

import java.util.List;

public interface DashboardService {
    DashboardOverviewDTO getOverview();

    PlacementStatsDTO getPlacementStats();

    TimesheetStatsDTO getTimesheetStatus();

    InvoiceStatsDTO getInvoiceStatus();

    ConsultantStatsDTO getActiveConsultants();

    RevenueMetricsDTO getRevenueMetrics();

    List<MonthlyTrendDTO> getPlacementTrends();

    PerformanceMetricsDTO getPerformanceMetrics();

    DashboardSummaryDTO getDashboardSummary();
}