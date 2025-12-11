package com.jobportal.serviceImpl;

import com.jobportal.dto.ApplicationDTO;
import com.jobportal.dto.ApplicationRequestDTO;
import com.jobportal.dto.ApplicationStatusDTO;
import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ApplicationDTO create(ApplicationRequestDTO applicationRequestDTO) {
        Job job = jobRepository.findById(applicationRequestDTO.getJobId())
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Job", "id", applicationRequestDTO.getJobId()));

        if (job.getStatus() != Job.JobStatus.OPEN) {
            throw ExceptionUtils.businessError("Cannot apply to a closed job");
        }

        // Get current user (from security context in real app)
        User consultant = userRepository.findById(1L)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("User", "id", 1L));

        // Validate user is a consultant
        if (consultant.getUserType() != User.UserType.CONSULTANT) {
            throw ExceptionUtils.businessError("Only consultants can apply for jobs");
        }

        // Validate consultant is active
        if (!consultant.getIsActive()) {
            throw ExceptionUtils.businessError("Consultant account is not active");
        }

        // Check if already applied
        if (applicationRepository.findByJobIdAndConsultantId(job.getJobId(), consultant.getUserId()).isPresent()) {
            throw ExceptionUtils.duplicateResource("Application", "job and consultant",
                    "Job: " + job.getJobId() + ", Consultant: " + consultant.getUserId());
        }

        // Validate resume URL is provided
        if (applicationRequestDTO.getResumeUrl() == null || applicationRequestDTO.getResumeUrl().trim().isEmpty()) {
            throw ExceptionUtils.validationError("Resume URL is required");
        }

        Application application = new Application();
        application.setJob(job);
        application.setConsultant(consultant);
        application.setCoverLetter(applicationRequestDTO.getCoverLetter());
        application.setResumeUrl(applicationRequestDTO.getResumeUrl());
        application.setStatus(Application.ApplicationStatus.PENDING);
        application.setAppliedDate(LocalDateTime.now());

        Application savedApplication = applicationRepository.save(application);

        // Notify recruiter
        notificationService.createNotification(
                job.getCreatedBy().getUserId(),
                "New Application Received",
                String.format("New application for job '%s' from %s %s",
                        job.getJobTitle(), consultant.getFirstName(), consultant.getLastName()),
                "APPLICATION",
                savedApplication.getApplicationId()
        );

        return ApplicationDTO.fromEntity(savedApplication);
    }

    @Override
    public ApplicationDTO getById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Application", "id", id));
        return ApplicationDTO.fromEntity(application);
    }

    @Override
    public List<ApplicationDTO> getAll() {
        return applicationRepository.findAll()
                .stream()
                .map(ApplicationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationDTO update(Long id, ApplicationRequestDTO applicationRequestDTO) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Application", "id", id));

        // Only allow updates to pending applications
        if (application.getStatus() != Application.ApplicationStatus.PENDING) {
            throw ExceptionUtils.businessError("Can only update pending applications");
        }

        // Update allowed fields
        if (applicationRequestDTO.getCoverLetter() != null) {
            application.setCoverLetter(applicationRequestDTO.getCoverLetter());
        }
        if (applicationRequestDTO.getResumeUrl() != null) {
            application.setResumeUrl(applicationRequestDTO.getResumeUrl());
        }

        Application updatedApplication = applicationRepository.save(application);
        return ApplicationDTO.fromEntity(updatedApplication);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Application", "id", id));

        // Only allow deletion of pending or rejected applications
        if (application.getStatus() == Application.ApplicationStatus.HIRED) {
            throw ExceptionUtils.businessError("Cannot delete a hired application");
        }

        if (application.getStatus() == Application.ApplicationStatus.SHORTLISTED) {
            throw ExceptionUtils.businessError("Cannot delete a shortlisted application");
        }

        applicationRepository.delete(application);
    }

    @Override
    @Transactional
    public ApplicationDTO updateStatus(Long id, ApplicationStatusDTO statusDTO) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Application", "id", id));

        Application.ApplicationStatus newStatus = statusDTO.getStatus();
        Application.ApplicationStatus oldStatus = application.getStatus();

        // Validate status transition
        validateStatusTransition(oldStatus, newStatus);

        application.setStatus(newStatus);
        application.setStatusNotes(statusDTO.getStatusNotes());
        application.setReviewDate(LocalDateTime.now());

        Application updatedApplication = applicationRepository.save(application);

        // Notify consultant about status change
        String statusMessage = getStatusMessage(newStatus, application.getJob().getJobTitle());
        notificationService.createNotification(
                application.getConsultant().getUserId(),
                "Application Status Updated",
                statusMessage,
                "APPLICATION",
                updatedApplication.getApplicationId()
        );

        return ApplicationDTO.fromEntity(updatedApplication);
    }

    @Override
    public List<ApplicationDTO> getByJobId(Long jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw ExceptionUtils.resourceNotFound("Job", "id", jobId);
        }

        return applicationRepository.findByJobJobId(jobId)
                .stream()
                .map(ApplicationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> getByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw ExceptionUtils.resourceNotFound("User", "id", userId);
        }

        return applicationRepository.findByConsultantUserId(userId)
                .stream()
                .map(ApplicationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> getByStatus(String status) {
        try {
            Application.ApplicationStatus applicationStatus =
                    Application.ApplicationStatus.valueOf(status.toUpperCase());

            return applicationRepository.findByStatus(applicationStatus)
                    .stream()
                    .map(ApplicationDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw ExceptionUtils.validationError("Invalid application status: " + status);
        }
    }

    @Override
    public Long getPendingApplicationCount() {
        return applicationRepository.countPendingApplications();
    }

    private void validateStatusTransition(Application.ApplicationStatus oldStatus,
                                          Application.ApplicationStatus newStatus) {
        switch (oldStatus) {
            case PENDING:
                if (!List.of(Application.ApplicationStatus.REVIEWED, Application.ApplicationStatus.REJECTED).contains(newStatus)) {
                    throw ExceptionUtils.businessError("Invalid status transition from PENDING to " + newStatus);
                }
                break;
            case REVIEWED:
                if (!List.of(Application.ApplicationStatus.SHORTLISTED, Application.ApplicationStatus.REJECTED).contains(newStatus)) {
                    throw ExceptionUtils.businessError("Invalid status transition from REVIEWED to " + newStatus);
                }
                break;
            case SHORTLISTED:
                if (!List.of(Application.ApplicationStatus.HIRED, Application.ApplicationStatus.REJECTED).contains(newStatus)) {
                    throw ExceptionUtils.businessError("Invalid status transition from SHORTLISTED to " + newStatus);
                }
                break;
            case HIRED:
            case REJECTED:
                throw ExceptionUtils.businessError("Cannot change status from " + oldStatus);
        }
    }

    private String getStatusMessage(Application.ApplicationStatus status, String jobTitle) {
        switch (status) {
            case REVIEWED:
                return String.format("Your application for '%s' has been reviewed and is under consideration", jobTitle);
            case SHORTLISTED:
                return String.format("Congratulations! Your application for '%s' has been shortlisted", jobTitle);
            case HIRED:
                return String.format("Congratulations! You have been hired for '%s'", jobTitle);
            case REJECTED:
                return String.format("Your application for '%s' has not been successful at this time", jobTitle);
            default:
                return String.format("Your application for '%s' status has been updated", jobTitle);
        }
    }
}