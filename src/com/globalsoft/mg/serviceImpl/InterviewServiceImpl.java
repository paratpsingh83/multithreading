package com.jobportal.serviceImpl;

import com.jobportal.dto.InterviewDTO;
import com.jobportal.dto.InterviewRequestDTO;
import com.jobportal.dto.InterviewStatusDTO;
import com.jobportal.entity.Application;
import com.jobportal.entity.Interview;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.InterviewRepository;
import com.jobportal.service.InterviewService;
import com.jobportal.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public InterviewDTO create(InterviewRequestDTO interviewRequestDTO) {
        Application application = applicationRepository.findById(interviewRequestDTO.getApplicationId())
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Application", "id", interviewRequestDTO.getApplicationId()));

        // Validate application status
        if (application.getStatus() != Application.ApplicationStatus.REVIEWED &&
                application.getStatus() != Application.ApplicationStatus.SHORTLISTED) {
            throw ExceptionUtils.businessError("Can only schedule interview for reviewed or shortlisted applications");
        }

        // Validate interview date is in the future
        if (interviewRequestDTO.getInterviewDate().isBefore(LocalDateTime.now())) {
            throw ExceptionUtils.validationError("Interview date must be in the future");
        }

        // Check for scheduling conflicts (same consultant within 2 hours)
        LocalDateTime interviewStart = interviewRequestDTO.getInterviewDate();
        LocalDateTime interviewEnd = interviewStart.plusHours(2);

        List<Interview> conflictingInterviews = interviewRepository
                .findByInterviewDateRange(interviewStart.minusHours(2), interviewEnd.plusHours(2))
                .stream()
                .filter(interview -> interview.getApplication().getConsultant().getUserId()
                        .equals(application.getConsultant().getUserId()))
                .collect(Collectors.toList());

        if (!conflictingInterviews.isEmpty()) {
            throw ExceptionUtils.businessError("Consultant has a conflicting interview scheduled during this time");
        }

        Interview interview = new Interview();
        interview.setApplication(application);
        interview.setInterviewDate(interviewRequestDTO.getInterviewDate());
        interview.setInterviewType(interviewRequestDTO.getInterviewType());
        interview.setStatus(Interview.InterviewStatus.SCHEDULED);

        Interview savedInterview = interviewRepository.save(interview);

        // Notify consultant about interview
        String interviewMessage = String.format(
                "Interview scheduled for %s. Type: %s. Job: %s",
                interviewRequestDTO.getInterviewDate().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a")),
                interviewRequestDTO.getInterviewType(),
                application.getJob().getJobTitle()
        );

        notificationService.createNotification(
                application.getConsultant().getUserId(),
                "Interview Scheduled",
                interviewMessage,
                "INTERVIEW",
                savedInterview.getInterviewId()
        );

        // Notify recruiter
        notificationService.createNotification(
                application.getJob().getCreatedBy().getUserId(),
                "Interview Scheduled",
                String.format("Interview scheduled for %s with %s %s",
                        application.getJob().getJobTitle(),
                        application.getConsultant().getFirstName(),
                        application.getConsultant().getLastName()),
                "INTERVIEW",
                savedInterview.getInterviewId()
        );

        return InterviewDTO.fromEntity(savedInterview);
    }

    @Override
    public InterviewDTO getById(Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Interview", "id", id));
        return InterviewDTO.fromEntity(interview);
    }

    @Override
    public List<InterviewDTO> getAll() {
        return interviewRepository.findAll()
                .stream()
                .map(InterviewDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InterviewDTO update(Long id, InterviewRequestDTO interviewRequestDTO) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Interview", "id", id));

        // Only allow updates to scheduled interviews
        if (interview.getStatus() != Interview.InterviewStatus.SCHEDULED) {
            throw ExceptionUtils.businessError("Can only update scheduled interviews");
        }

        // Update interview date if provided
        if (interviewRequestDTO.getInterviewDate() != null) {
            if (interviewRequestDTO.getInterviewDate().isBefore(LocalDateTime.now())) {
                throw ExceptionUtils.validationError("Interview date must be in the future");
            }
            interview.setInterviewDate(interviewRequestDTO.getInterviewDate());
        }

        // Update interview type if provided
        if (interviewRequestDTO.getInterviewType() != null) {
            interview.setInterviewType(interviewRequestDTO.getInterviewType());
        }

        Interview updatedInterview = interviewRepository.save(interview);

        // Notify about interview update
        notificationService.createNotification(
                interview.getApplication().getConsultant().getUserId(),
                "Interview Updated",
                "Your interview details have been updated. Please check the new schedule.",
                "INTERVIEW",
                updatedInterview.getInterviewId()
        );

        return InterviewDTO.fromEntity(updatedInterview);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Interview", "id", id));

        if (interview.getStatus() != Interview.InterviewStatus.SCHEDULED) {
            throw ExceptionUtils.businessError("Can only delete scheduled interviews");
        }

        // Notify about cancellation
        notificationService.createNotification(
                interview.getApplication().getConsultant().getUserId(),
                "Interview Cancelled",
                String.format("Interview for %s has been cancelled",
                        interview.getApplication().getJob().getJobTitle()),
                "INTERVIEW",
                interview.getInterviewId()
        );

        interviewRepository.delete(interview);
    }

    @Override
    @Transactional
    public InterviewDTO updateStatus(Long id, InterviewStatusDTO statusDTO) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Interview", "id", id));

        Interview.InterviewStatus newStatus = statusDTO.getStatus();
        Interview.InterviewStatus oldStatus = interview.getStatus();

        // Validate status transition
        validateInterviewStatusTransition(oldStatus, newStatus);

        interview.setStatus(newStatus);
        interview.setFeedback(statusDTO.getFeedback());
        interview.setRating(statusDTO.getRating());

        Interview updatedInterview = interviewRepository.save(interview);

        // Update application status based on interview result if completed
        if (newStatus == Interview.InterviewStatus.COMPLETED) {
            updateApplicationAfterInterview(interview.getApplication(), statusDTO.getRating(), statusDTO.getFeedback());
        }

        // Notify about status change
        String notificationMessage = String.format(
                "Interview status changed to %s for %s",
                newStatus, interview.getApplication().getJob().getJobTitle()
        );

        if (statusDTO.getFeedback() != null && !statusDTO.getFeedback().isEmpty()) {
            notificationMessage += ". Feedback: " + statusDTO.getFeedback();
        }

        notificationService.createNotification(
                interview.getApplication().getConsultant().getUserId(),
                "Interview Status Updated",
                notificationMessage,
                "INTERVIEW",
                updatedInterview.getInterviewId()
        );

        return InterviewDTO.fromEntity(updatedInterview);
    }

    @Override
    public List<InterviewDTO> getByApplicationId(Long applicationId) {
        if (!applicationRepository.existsById(applicationId)) {
            throw ExceptionUtils.resourceNotFound("Application", "id", applicationId);
        }

        return interviewRepository.findByApplicationApplicationId(applicationId)
                .stream()
                .map(InterviewDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterviewDTO> getUpcomingInterviews() {
        return interviewRepository.findByInterviewDateRange(
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(7)
                )
                .stream()
                .filter(interview -> interview.getStatus() == Interview.InterviewStatus.SCHEDULED)
                .map(InterviewDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void validateInterviewStatusTransition(Interview.InterviewStatus oldStatus,
                                                   Interview.InterviewStatus newStatus) {
        if (oldStatus == Interview.InterviewStatus.COMPLETED || oldStatus == Interview.InterviewStatus.CANCELLED) {
            throw ExceptionUtils.businessError("Cannot change status of completed or cancelled interview");
        }

        if (oldStatus == newStatus) {
            throw ExceptionUtils.businessError("Interview is already in " + newStatus + " status");
        }

        // Specific validations
        if (oldStatus == Interview.InterviewStatus.SCHEDULED && newStatus == Interview.InterviewStatus.COMPLETED) {
            // Allow scheduled to completed only if interview date has passed
            // This would be checked in the repository query in real implementation
        }
    }

    private void updateApplicationAfterInterview(Application application, Integer rating, String feedback) {
        if (rating != null) {
            if (rating >= 4) {
                // Good rating, move to next stage
                if (application.getStatus() == Application.ApplicationStatus.REVIEWED) {
                    application.setStatus(Application.ApplicationStatus.SHORTLISTED);
                    application.setStatusNotes("Excellent performance in interview. Rating: " + rating);
                } else if (application.getStatus() == Application.ApplicationStatus.SHORTLISTED) {
                    application.setStatus(Application.ApplicationStatus.HIRED);
                    application.setStatusNotes("Outstanding final interview. Rating: " + rating);
                }
            } else if (rating <= 2) {
                // Poor rating, reject
                application.setStatus(Application.ApplicationStatus.REJECTED);
                application.setStatusNotes("Did not meet interview expectations. Rating: " + rating);
                if (feedback != null && !feedback.isEmpty()) {
                    application.setStatusNotes(application.getStatusNotes() + ". Feedback: " + feedback);
                }
            }
            // Rating 3 might require additional interviews or review

            applicationRepository.save(application);
        }
    }
}