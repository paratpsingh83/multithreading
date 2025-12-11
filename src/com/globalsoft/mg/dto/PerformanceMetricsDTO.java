package com.jobportal.dto;

import lombok.Data;
import java.util.Map;

@Data
public class PerformanceMetricsDTO {
    private Double placementSuccessRate;
    private Double interviewToPlacementRate;
    private Double timesheetApprovalRate;
    private Double invoiceCollectionRate;
    private Integer avgTimeToFill;
    private Integer avgTimeToHire;
    
    private Map<String, Double> metricsByMonth;
}