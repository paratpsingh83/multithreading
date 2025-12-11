package com.jobportal.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class RevenueMetricsDTO {
    private Double totalRevenue;
    private Double revenueThisMonth;
    private Double revenueThisQuarter;
    private Double outstandingRevenue;
    private Map<String, Double> revenueByMonth; // Last 6 months
    private List<RevenueTrendDTO> revenueTrends;
    
    @Data
    public static class RevenueTrendDTO {
        private String period;
        private Double revenue;
        private Double growth;
    }
}