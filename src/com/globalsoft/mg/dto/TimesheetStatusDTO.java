package com.jobportal.dto;

import com.jobportal.entity.Timesheet;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TimesheetStatusDTO {
    @NotNull(message = "Status is required")
    private Timesheet.TimesheetStatus status;
    
    private String rejectionReason;

}