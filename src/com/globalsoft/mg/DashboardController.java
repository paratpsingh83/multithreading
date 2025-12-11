package com.globalsoft.mg;

import com.jobportal.dto.*;
import com.jobportal.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/api/v1/overview")
    public ResponseEntity<ApiResponseDTO<DashboardOverviewDTO>> getOverview() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getOverview(),"Dashboard overview retrieved successfully"));
    }

    @GetMapping("/api/v1/placement-stats")
    public ResponseEntity<ApiResponseDTO<PlacementStatsDTO>> getPlacementStats() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getPlacementStats(),"Placement stats retrieved successfully"));
    }

    @GetMapping("/api/v1/timesheet-status")
    public ResponseEntity<ApiResponseDTO<TimesheetStatsDTO>> getTimesheetStatus() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getTimesheetStatus(),"Timesheet status retrieved successfully"));
    }

    @GetMapping("/api/v1/invoice-status")
    public ResponseEntity<ApiResponseDTO<InvoiceStatsDTO>> getInvoiceStatus() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getInvoiceStatus(),"Invoice status retrieved successfully"));
    }

    @GetMapping("/api/v1/active-consultants")
    public ResponseEntity<ApiResponseDTO<ConsultantStatsDTO>> getActiveConsultants() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getActiveConsultants(),"Active consultants retrieved successfully"));
    }

    @GetMapping("/api/v1/revenue-metrics")
    public ResponseEntity<ApiResponseDTO<RevenueMetricsDTO>> getRevenueMetrics() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getRevenueMetrics(),"Revenue metrics retrieved successfully"));
    }

    @GetMapping("/api/v1/placement-trends")
    public ResponseEntity<ApiResponseDTO<List<MonthlyTrendDTO>>> getPlacementTrends() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success(service.getPlacementTrends(),"Placement trends retrieved successfully"));
    }

    @GetMapping("/api/v1/performance-metrics")
    public ResponseEntity<ApiResponseDTO<PerformanceMetricsDTO>> getPerformanceMetrics() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getPerformanceMetrics(),"Performance metrics retrieved successfully"));
    }
}