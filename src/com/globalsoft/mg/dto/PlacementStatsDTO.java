package com.jobportal.dto;

import lombok.Data;
import java.util.Map;

@Data
public class PlacementStatsDTO {
    private Map<String, Long> placementsByType; // FULL_TIME, CONTRACT
    private Map<String, Long> placementsByLocation; // US, Canada, etc.
    private Map<String, Long> placementsByStatus; // ACTIVE, COMPLETED, etc.
    private Long totalPlacements;
    private Long activePlacements;
    private Long newPlacementsThisMonth;
}