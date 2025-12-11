package com.jobportal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TimesheetEntryRequestDTO {
    @NotNull(message = "Work date is required")
    private LocalDate workDate;
    
    @NotNull(message = "Hours worked is required")
    private Double hoursWorked;
    
    private String taskDescription;
    private String projectCode;
    private Boolean isBillable = true;
}