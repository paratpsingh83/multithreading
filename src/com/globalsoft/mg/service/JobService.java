package com.jobportal.service;

import com.jobportal.dto.*;

import java.util.List;

public interface JobService {
    JobDTO create(JobRequestDTO jobRequestDTO);

    JobDTO getById(Long id);

    List<JobDTO> getAll();

    JobDTO update(Long id, JobRequestDTO jobRequestDTO);

    void delete(Long id);

    PagedResponseDTO<JobDTO> search(JobSearchDTO searchDTO);

    List<JobDTO> getOpenJobs();

    List<ApplicationDTO> getJobApplications(Long jobId);

    JobDTO updateStatus(Long id, String status);

    List<JobDTO> getJobsByCompany(Long companyId);
}