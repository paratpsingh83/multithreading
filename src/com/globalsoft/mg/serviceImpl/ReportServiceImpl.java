//package com.jobportal.serviceImpl;
//
//import com.jobportal.dto.*;
//import com.jobportal.repository.*;
//import com.jobportal.service.ReportService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.YearMonth;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class ReportServiceImpl implements ReportService {
//
//    private final PlacementRepository placementRepository;
//    private final TimesheetRepository timesheetRepository;
//    private final InvoiceRepository invoiceRepository;
//    private final PaymentRepository paymentRepository;
//    private final ApplicationRepository applicationRepository;
//    private final UserRepository userRepository;
//
//    @Override
//    public List<PlacementDTO> getPlacementReport() {
//        return placementRepository.findAll()
//                .stream()
//                .map(PlacementDTO::fromEntity)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<TimesheetDTO> getTimesheetReport() {
//        // Get timesheets from last 30 days
//        LocalDate startDate = LocalDate.now().minusDays(30);
//        LocalDate endDate = LocalDate.now();
//
//        return timesheetRepository.findByDateRange(startDate, endDate)
//                .stream()
//                .map(timesheet -> {
//                    // Load entries for each timesheet
//                    timesheet.setEntries(timesheetEntryRepository.findByTimesheetTimesheetId(timesheet.getTimesheetId()));
//                    return TimesheetDTO.fromEntity(timesheet);
//                })
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<InvoiceDTO> getInvoiceReport() {
//        return invoiceRepository.findAll()
//                .stream()
//                .map(invoice -> {
//                    // Load items for each invoice
//                    invoice.setItems(invoiceItemRepository.findByInvoiceInvoiceId(invoice.getInvoiceId()));
//                    return InvoiceDTO.fromEntity(invoice);
//                })
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public RevenueMetricsDTO getFinancialReport() {
//        RevenueMetricsDTO metrics = new RevenueMetricsDTO();
//
//        // Total revenue (all time)
//        Double totalRevenue = paymentRepository.sumPaymentsSince(LocalDate.of(2000, 1, 1));
//        metrics.setTotalRevenue(totalRevenue != null ? totalRevenue : 0.0);
//
//        // This month's revenue
//        Double revenueThisMonth = paymentRepository.sumPaymentsSince(LocalDate.now().withDayOfMonth(1));
//        metrics.setRevenueThisMonth(revenueThisMonth != null ? revenueThisMonth : 0.0);
//
//        // This quarter's revenue
//        LocalDate quarterStart = LocalDate.now().withMonth(((LocalDate.now().getMonthValue() - 1) / 3) * 3 + 1)
//                .withDayOfMonth(1);
//        Double revenueThisQuarter = paymentRepository.sumPaymentsSince(quarterStart);
//        metrics.setRevenueThisQuarter(revenueThisQuarter != null ? revenueThisQuarter : 0.0);
//
//        // Outstanding revenue
//        Double outstandingRevenue = invoiceRepository.sumOutstandingAmount();
//        metrics.setOutstandingRevenue(outstandingRevenue != null ? outstandingRevenue : 0.0);
//
//        // Revenue by month for last 12 months
//        Map<String, Double> revenueByMonth = new LinkedHashMap<>();
//        for (int i = 11; i >= 0; i--) {
//            YearMonth month = YearMonth.now().minusMonths(i);
//            LocalDate monthStart = month.atDay(1);
//            LocalDate monthEnd = month.atEndOfMonth();
//
//            Double monthlyRevenue = paymentRepository.findByPaymentDateRange(monthStart, monthEnd)
//                    .stream()
//                    .mapToDouble(payment -> payment.getAmountPaid())
//                    .sum();
//
//            revenueByMonth.put(month.getMonth().toString() + " " + month.getYear(), monthlyRevenue);
//        }
//        metrics.setRevenueByMonth(revenueByMonth);
//
//        return metrics;
//    }
//
//    @Override
//    public String generateReport(String reportType, String format) {
//        // Validate report type
//        if (reportType == null || reportType.trim().isEmpty()) {
//            throw ExceptionUtils.validationError("Report type is required");
//        }
//
//        // Validate format
//        if (format == null || format.trim().isEmpty()) {
//            throw ExceptionUtils.validationError("Report format is required");
//        }
//
//        List<String> supportedFormats = List.of("PDF", "EXCEL", "CSV");
//        if (!supportedFormats.contains(format.toUpperCase())) {
//            throw ExceptionUtils.validationError(
//                    "Unsupported format. Supported formats: " + String.join(", ", supportedFormats)
//            );
//        }
//
//        // Generate report based on type and format
//        String reportName;
//        switch (reportType.toUpperCase()) {
//            case "PLACEMENT":
//                reportName = generatePlacementReport(format);
//                break;
//            case "TIMESHEET":
//                reportName = generateTimesheetReport(format);
//                break;
//            case "INVOICE":
//                reportName = generateInvoiceReport(format);
//                break;
//            case "FINANCIAL":
//                reportName = generateFinancialReport(format);
//                break;
//            case "CONSULTANT":
//                reportName = generateConsultantReport(format);
//                break;
//            default:
//                throw ExceptionUtils.validationError(
//                        "Unsupported report type. Supported types: PLACEMENT, TIMESHEET, INVOICE, FINANCIAL, CONSULTANT"
//                );
//        }
//
//        return reportName;
//    }
//
//    // Additional comprehensive reports
//
//    public ConsultantPerformanceReportDTO getConsultantPerformanceReport() {
//        ConsultantPerformanceReportDTO report = new ConsultantPerformanceReportDTO();
//
//        // Get all consultants
//        List<User> consultants = userRepository.findAllActiveConsultants();
//
//        List<ConsultantPerformanceDTO> performances = consultants.stream()
//                .map(consultant -> {
//                    ConsultantPerformanceDTO performance = new ConsultantPerformanceDTO();
//                    performance.setConsultant(UserDTO.fromEntity(consultant));
//
//                    // Get placements for this consultant
//                    List<Placement> consultantPlacements = placementRepository.findByConsultantId(consultant.getUserId());
//                    performance.setTotalPlacements((long) consultantPlacements.size());
//
//                    // Get active placements
//                    long activePlacements = consultantPlacements.stream()
//                            .filter(p -> p.getPlacementStatus() == com.jobportal.entity.Placement.PlacementStatus.ACTIVE)
//                            .count();
//                    performance.setActivePlacements(activePlacements);
//
//                    // Calculate total hours worked
//                    Double totalHours = timesheetRepository.findByConsultantUserId(consultant.getUserId())
//                            .stream()
//                            .filter(t -> t.getStatus() == com.jobportal.entity.Timesheet.TimesheetStatus.APPROVED)
//                            .mapToDouble(t -> t.getTotalHours() != null ? t.getTotalHours() : 0.0)
//                            .sum();
//                    performance.setTotalHoursWorked(totalHours);
//
//                    // Calculate total revenue generated
//                    Double totalRevenue = consultantPlacements.stream()
//                            .mapToDouble(p -> {
//                                // Simplified calculation - in real app, use invoice amounts
//                                return p.getBillingRate() != null ? p.getBillingRate() * 160 : 0.0; // 160 hours/month estimate
//                            })
//                            .sum();
//                    performance.setTotalRevenueGenerated(totalRevenue);
//
//                    return performance;
//                })
//                .collect(Collectors.toList());
//
//        report.setConsultantPerformances(performances);
//        report.setGeneratedDate(LocalDate.now());
//
//        return report;
//    }
//
//    public MonthlyPerformanceReportDTO getMonthlyPerformanceReport(int year, int month) {
//        MonthlyPerformanceReportDTO report = new MonthlyPerformanceReportDTO();
//
//        YearMonth targetMonth = YearMonth.of(year, month);
//        LocalDate monthStart = targetMonth.atDay(1);
//        LocalDate monthEnd = targetMonth.atEndOfMonth();
//
//        report.setMonth(targetMonth);
//        report.setTotalPlacements(placementRepository.countPlacementsSince(monthStart));
//        report.setNewConsultants(userRepository.countNewConsultantsThisMonth());
//
//        // Financial metrics
//        Double monthlyRevenue = paymentRepository.sumPaymentsSince(monthStart);
//        report.setMonthlyRevenue(monthlyRevenue != null ? monthlyRevenue : 0.0);
//
//        // Application metrics
//        report.setTotalApplications(applicationRepository.count());
//        report.setApplicationConversionRate(calculateConversionRate(monthStart, monthEnd));
//
//        return report;
//    }
//
//    private String generatePlacementReport(String format) {
//        // In real implementation, this would use a reporting library like JasperReports
//        // For now, return a placeholder filename
//        return "placement_report_" + System.currentTimeMillis() + "." + format.toLowerCase();
//    }
//
//    private String generateTimesheetReport(String format) {
//        return "timesheet_report_" + System.currentTimeMillis() + "." + format.toLowerCase();
//    }
//
//    private String generateInvoiceReport(String format) {
//        return "invoice_report_" + System.currentTimeMillis() + "." + format.toLowerCase();
//    }
//
//    private String generateFinancialReport(String format) {
//        return "financial_report_" + System.currentTimeMillis() + "." + format.toLowerCase();
//    }
//
//    private String generateConsultantReport(String format) {
//        return "consultant_report_" + System.currentTimeMillis() + "." + format.toLowerCase();
//    }
//
//    private double calculateConversionRate(LocalDate startDate, LocalDate endDate) {
//        long totalApplications = applicationRepository.count();
//        long successfulPlacements = placementRepository.countPlacementsSince(startDate);
//
//        if (totalApplications == 0) {
//            return 0.0;
//        }
//
//        return (double) successfulPlacements / totalApplications * 100;
//    }
//
//    // Additional DTOs for comprehensive reporting
//    public static class ConsultantPerformanceReportDTO {
//        private List<ConsultantPerformanceDTO> consultantPerformances;
//        private LocalDate generatedDate;
//
//        // getters and setters
//    }
//
//    public static class ConsultantPerformanceDTO {
//        private UserDTO consultant;
//        private Long totalPlacements;
//        private Long activePlacements;
//        private Double totalHoursWorked;
//        private Double totalRevenueGenerated;
//
//        // getters and setters
//    }
//
//    public static class MonthlyPerformanceReportDTO {
//        private YearMonth month;
//        private Long totalPlacements;
//        private Long newConsultants;
//        private Double monthlyRevenue;
//        private Long totalApplications;
//        private Double applicationConversionRate;
//
//        // getters and setters
//    }
//}