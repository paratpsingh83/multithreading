package com.jobportal.dto;

import com.jobportal.entity.Job;
import lombok.Data;
import java.util.List;

@Data
public class JobSearchDTO {
    private String keyword;
    private List<Job.JobType> jobTypes;
    private List<String> locations;
    private Double minSalary;
    private Double maxSalary;
    private Boolean remoteOnly;
    private List<String> skills;
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}