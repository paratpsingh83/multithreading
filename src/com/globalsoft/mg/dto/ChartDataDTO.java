package com.jobportal.dto;

import lombok.Data;

@Data
public class ChartDataDTO {
    private String label;
    private Long value;
    private Double percentage;
    
    public ChartDataDTO(String label, Long value) {
        this.label = label;
        this.value = value;
    }
    
    public ChartDataDTO(String label, Long value, Double percentage) {
        this.label = label;
        this.value = value;
        this.percentage = percentage;
    }
}