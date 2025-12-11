package com.jobportal.dto;

import lombok.Data;
import java.util.Map;

@Data
public class LocationStatsDTO {
    private Map<String, Long> placementsByLocation;
    private Map<String, Long> consultantsByLocation;
    private Map<String, Long> jobsByLocation;
    
    // Top locations
    private String topPlacementLocation;
    private String topConsultantLocation;
    private String topJobLocation;
}