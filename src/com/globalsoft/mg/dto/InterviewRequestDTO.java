package com.jobportal.dto;

import com.jobportal.entity.Interview;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InterviewRequestDTO {
    @NotNull(message = "Application ID is required")
    private Long applicationId;
    
    @NotNull(message = "Interview date is required")
    private LocalDateTime interviewDate;
    
    @NotNull(message = "Interview type is required")
    private Interview.InterviewType interviewType;
    
    private List<String> interviewers;
}