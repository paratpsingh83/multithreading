package com.jobportal.dto;

import lombok.Data;

@Data
public class MonthlyTrendDTO {
    private String month;
    private Long placements;
    private Long applications;
    private Double revenue;
    private Long newConsultants;
    
    public MonthlyTrendDTO(String month, Long placements, Long applications, Double revenue, Long newConsultants) {
        this.month = month;
        this.placements = placements;
        this.applications = applications;
        this.revenue = revenue;
        this.newConsultants = newConsultants;
    }
}