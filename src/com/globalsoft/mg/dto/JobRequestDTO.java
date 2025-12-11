package com.jobportal.dto;

import com.jobportal.entity.Job;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class JobRequestDTO {
    @NotNull(message = "Company ID is required")
    private Long companyId;
    
    @NotBlank(message = "Job title is required")
    private String jobTitle;
    
    @NotBlank(message = "Job description is required")
    private String jobDescription;
    
    @NotNull(message = "Job type is required")
    private Job.JobType jobType;
    
    private String experienceLevel;
    private Double salaryRangeMin;
    private Double salaryRangeMax;
    private String location;
    private Boolean remoteOption = false;
    private List<String> skillsRequired;
    private Job.JobStatus status = Job.JobStatus.DRAFT;
}