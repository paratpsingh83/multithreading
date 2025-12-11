package com.jobportal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class TimesheetRequestDTO {
    @NotNull(message = "Placement ID is required")
    private Long placementId;
    
    @NotNull(message = "Week start date is required")
    private LocalDate weekStartDate;
    
    @NotNull(message = "Week end date is required")
    private LocalDate weekEndDate;
    
    private List<TimesheetEntryRequestDTO> entries;
}