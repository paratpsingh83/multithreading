package com.jobportal.dto;

import com.jobportal.entity.Job;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobDTO {
    private Long jobId;
    private CompanyDTO company;
    private String jobTitle;
    private String jobDescription;
    private Job.JobType jobType;
    private String experienceLevel;
    private Double salaryRangeMin;
    private Double salaryRangeMax;
    private String location;
    private Boolean remoteOption;
    private List<String> skillsRequired;
    private Job.JobStatus status;
    private UserDTO createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long applicationCount;
    
    public static JobDTO fromEntity(Job job) {
        JobDTO dto = new JobDTO();
        dto.setJobId(job.getJobId());
        dto.setCompany(CompanyDTO.fromEntity(job.getCompany()));
        dto.setJobTitle(job.getJobTitle());
        dto.setJobDescription(job.getJobDescription());
        dto.setJobType(job.getJobType());
        dto.setExperienceLevel(job.getExperienceLevel());
        dto.setSalaryRangeMin(job.getSalaryRangeMin());
        dto.setSalaryRangeMax(job.getSalaryRangeMax());
        dto.setLocation(job.getLocation());
        dto.setRemoteOption(job.getRemoteOption());
        dto.setStatus(job.getStatus());
        dto.setCreatedBy(UserDTO.fromEntity(job.getCreatedBy()));
        dto.setCreatedAt(job.getCreatedAt());
        dto.setUpdatedAt(job.getUpdatedAt());
        dto.setApplicationCount((long) job.getApplications().size());
        return dto;
    }
}