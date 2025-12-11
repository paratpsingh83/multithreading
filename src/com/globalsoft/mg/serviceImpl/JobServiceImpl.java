package com.jobportal.serviceImpl;

import com.jobportal.dto.*;
import com.jobportal.entity.Company;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ApplicationService applicationService;

    @Override
    @Transactional
    public JobDTO create(JobRequestDTO jobRequestDTO) {
        // Validate company exists
        Company company = companyRepository.findById(jobRequestDTO.getCompanyId())
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Company", "id", jobRequestDTO.getCompanyId()));

        // Validate company is active
        if (company.getStatus() != Company.CompanyStatus.ACTIVE) {
            throw ExceptionUtils.businessError("Cannot create job for inactive company");
        }

        // Get current user (from security context in real app)
        User currentUser = userRepository.findById(1L)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("User", "id", 1L));

        // Validate user has permission to create jobs
        if (currentUser.getUserType() != User.UserType.RECRUITER &&
                currentUser.getUserType() != User.UserType.ADMIN) {
            throw ExceptionUtils.businessError("Only recruiters and admins can create jobs");
        }

        Job job = new Job();
        job.setCompany(company);
        job.setJobTitle(jobRequestDTO.getJobTitle());
        job.setJobDescription(jobRequestDTO.getJobDescription());
        job.setJobType(jobRequestDTO.getJobType());
        job.setExperienceLevel(jobRequestDTO.getExperienceLevel());
        job.setSalaryRangeMin(jobRequestDTO.getSalaryRangeMin());
        job.setSalaryRangeMax(jobRequestDTO.getSalaryRangeMax());
        job.setLocation(jobRequestDTO.getLocation());
        job.setRemoteOption(jobRequestDTO.getRemoteOption() != null ? jobRequestDTO.getRemoteOption() : false);
        job.setStatus(jobRequestDTO.getStatus() != null ? jobRequestDTO.getStatus() : Job.JobStatus.DRAFT);
        job.setCreatedBy(currentUser);

        // Validate salary range
        if (jobRequestDTO.getSalaryRangeMin() != null && jobRequestDTO.getSalaryRangeMax() != null) {
            if (jobRequestDTO.getSalaryRangeMin() > jobRequestDTO.getSalaryRangeMax()) {
                throw ExceptionUtils.validationError("Minimum salary cannot be greater than maximum salary");
            }
        }

        Job savedJob = jobRepository.save(job);
        return JobDTO.fromEntity(savedJob);
    }

    @Override
    public JobDTO getById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Job", "id", id));
        return JobDTO.fromEntity(job);
    }

    @Override
    public List<JobDTO> getAll() {
        return jobRepository.findAll()
                .stream()
                .map(JobDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobDTO update(Long id, JobRequestDTO jobRequestDTO) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Job", "id", id));

        // Only allow updates to draft jobs
        if (job.getStatus() != Job.JobStatus.DRAFT) {
            throw ExceptionUtils.businessError("Can only update draft jobs");
        }

        // Update company if provided
        if (jobRequestDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(jobRequestDTO.getCompanyId())
                    .orElseThrow(() -> ExceptionUtils.resourceNotFound("Company", "id", jobRequestDTO.getCompanyId()));
            job.setCompany(company);
        }

        // Update other fields
        if (jobRequestDTO.getJobTitle() != null) {
            job.setJobTitle(jobRequestDTO.getJobTitle());
        }
        if (jobRequestDTO.getJobDescription() != null) {
            job.setJobDescription(jobRequestDTO.getJobDescription());
        }
        if (jobRequestDTO.getJobType() != null) {
            job.setJobType(jobRequestDTO.getJobType());
        }
        if (jobRequestDTO.getExperienceLevel() != null) {
            job.setExperienceLevel(jobRequestDTO.getExperienceLevel());
        }
        if (jobRequestDTO.getSalaryRangeMin() != null) {
            job.setSalaryRangeMin(jobRequestDTO.getSalaryRangeMin());
        }
        if (jobRequestDTO.getSalaryRangeMax() != null) {
            job.setSalaryRangeMax(jobRequestDTO.getSalaryRangeMax());
        }
        if (jobRequestDTO.getLocation() != null) {
            job.setLocation(jobRequestDTO.getLocation());
        }
        if (jobRequestDTO.getRemoteOption() != null) {
            job.setRemoteOption(jobRequestDTO.getRemoteOption());
        }
        if (jobRequestDTO.getStatus() != null) {
            job.setStatus(jobRequestDTO.getStatus());
        }

        // Validate salary range
        if (job.getSalaryRangeMin() != null && job.getSalaryRangeMax() != null) {
            if (job.getSalaryRangeMin() > job.getSalaryRangeMax()) {
                throw ExceptionUtils.validationError("Minimum salary cannot be greater than maximum salary");
            }
        }

        Job updatedJob = jobRepository.save(job);
        return JobDTO.fromEntity(updatedJob);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Job", "id", id));

        // Only allow deletion of draft jobs
        if (job.getStatus() != Job.JobStatus.DRAFT) {
            throw ExceptionUtils.businessError("Can only delete draft jobs");
        }

        // Check if job has applications
        if (!job.getApplications().isEmpty()) {
            throw ExceptionUtils.businessError("Cannot delete job with applications");
        }

        jobRepository.delete(job);
    }

    @Override
    public PagedResponseDTO<JobDTO> search(JobSearchDTO searchDTO) {
        // Validate page and size
        if (searchDTO.getPage() < 0) {
            throw ExceptionUtils.validationError("Page must be greater than or equal to 0");
        }
        if (searchDTO.getSize() <= 0 || searchDTO.getSize() > 100) {
            throw ExceptionUtils.validationError("Size must be between 1 and 100");
        }

        // Create sort
        Sort sort = Sort.by(
                searchDTO.getSortDirection().equalsIgnoreCase("DESC") ?
                        Sort.Direction.DESC : Sort.Direction.ASC,
                getValidSortField(searchDTO.getSortBy())
        );

        Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), sort);

        // Perform search
        Page<Job> jobPage = jobRepository.searchJobs(
                searchDTO.getKeyword(),
                searchDTO.getJobTypes() != null && !searchDTO.getJobTypes().isEmpty() ?
                        searchDTO.getJobTypes().get(0) : null,
                searchDTO.getLocations() != null && !searchDTO.getLocations().isEmpty() ?
                        searchDTO.getLocations().get(0) : null,
                searchDTO.getRemoteOnly(),
                pageable
        );

        List<JobDTO> jobDTOs = jobPage.getContent()
                .stream()
                .map(JobDTO::fromEntity)
                .collect(Collectors.toList());

        return new PagedResponseDTO<>(
                jobDTOs,
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages(),
                jobPage.isLast()
        );
    }

    @Override
    public List<JobDTO> getOpenJobs() {
        return jobRepository.findOpenJobs()
                .stream()
                .map(JobDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> getJobApplications(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Job", "id", jobId));

        return applicationService.getByJobId(jobId);
    }

    @Override
    @Transactional
    public JobDTO updateStatus(Long id, String status) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Job", "id", id));

        try {
            Job.JobStatus newStatus = Job.JobStatus.valueOf(status.toUpperCase());

            // Validate status transition
            validateJobStatusTransition(job.getStatus(), newStatus);

            job.setStatus(newStatus);
            Job updatedJob = jobRepository.save(job);
            return JobDTO.fromEntity(updatedJob);
        } catch (IllegalArgumentException e) {
            throw ExceptionUtils.validationError("Invalid job status: " + status);
        }
    }

    @Override
    public List<JobDTO> getJobsByCompany(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw ExceptionUtils.resourceNotFound("Company", "id", companyId);
        }

        return jobRepository.findByCompanyCompanyId(companyId)
                .stream()
                .map(JobDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private String getValidSortField(String sortBy) {
        // Whitelist of allowed sort fields
        List<String> allowedFields = List.of("createdAt", "updatedAt", "jobTitle", "salaryRangeMin", "salaryRangeMax");
        return allowedFields.contains(sortBy) ? sortBy : "createdAt";
    }

    private void validateJobStatusTransition(Job.JobStatus oldStatus, Job.JobStatus newStatus) {
        if (oldStatus == Job.JobStatus.CLOSED && newStatus == Job.JobStatus.OPEN) {
            throw ExceptionUtils.businessError("Cannot reopen a closed job");
        }

        if (oldStatus == Job.JobStatus.OPEN && newStatus == Job.JobStatus.DRAFT) {
            throw ExceptionUtils.businessError("Cannot move open job back to draft");
        }
    }
}