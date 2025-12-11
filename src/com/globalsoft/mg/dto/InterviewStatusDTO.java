package com.jobportal.dto;

import com.jobportal.entity.Interview;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InterviewStatusDTO {
    @NotNull(message = "Status is required")
    private Interview.InterviewStatus status;
    
    private String feedback;
    private Integer rating;
}