package com.jobportal.dto;

import com.jobportal.entity.Application;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationStatusDTO {
    @NotNull(message = "Status is required")
    private Application.ApplicationStatus status;
    
    private String statusNotes;
}