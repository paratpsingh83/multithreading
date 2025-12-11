package com.jobportal.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ConsultantStatsDTO {
    private Long totalConsultants;
    private Long activeConsultants;
    private Long newConsultantsThisMonth;
    private Map<String, Long> consultantsByStatus; // ACTIVE, INACTIVE, etc.
    private Map<String, Long> consultantsBySkill; // Top skills
}