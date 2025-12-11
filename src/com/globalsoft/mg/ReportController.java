//package com.jobportal.controller;
//
//import com.jobportal.dto.*;
//import com.jobportal.service.ReportService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/reports")
//@RequiredArgsConstructor
//public class ReportController {
//
//    private final ReportService service;
//
//    @GetMapping("/api/v1/placements")
//    public ResponseEntity<ApiResponseDTO<List<PlacementDTO>>> getPlacementReport() {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponseDTO.success( service.getPlacementReport(),"Placement report generated successfully"));
//    }
//
//    @GetMapping("/api/v1/timesheets")
//    public ResponseEntity<ApiResponseDTO<List<TimesheetDTO>>> getTimesheetReport() {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponseDTO.success( service.getTimesheetReport(),"Timesheet report generated successfully"));
//    }
//
//    @GetMapping("/api/v1/invoices")
//    public ResponseEntity<ApiResponseDTO<List<InvoiceDTO>>> getInvoiceReport() {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponseDTO.success( service.getInvoiceReport(),"Invoice report generated successfully"));
//    }
//
//    @GetMapping("/api/v1/financial")
//    public ResponseEntity<ApiResponseDTO<RevenueMetricsDTO>> getFinancialReport() {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponseDTO.success( service.getFinancialReport(),"Financial report generated successfully"));
//    }
//
//    @PostMapping("/api/v1/generate")
//    public ResponseEntity<ApiResponseDTO<String>> generateReport(@RequestParam String reportType,
//                                                                 @RequestParam String format) {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponseDTO.success( service.generateReport(reportType, format),"Report generated successfully"));
//    }
//}