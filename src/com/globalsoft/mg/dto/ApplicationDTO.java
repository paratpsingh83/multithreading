package com.jobportal.dto;

import com.jobportal.entity.Application;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApplicationDTO {
    private Long applicationId;
    private JobDTO job;
    private UserDTO consultant;
    private String coverLetter;
    private String resumeUrl;
    private Application.ApplicationStatus status;
    private LocalDateTime appliedDate;
    private LocalDateTime reviewDate;
    private String statusNotes;
    private PlacementDTO placement;
    private List<InterviewDTO> interviews;
    
    public static ApplicationDTO fromEntity(Application application) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setApplicationId(application.getApplicationId());
        dto.setJob(JobDTO.fromEntity(application.getJob()));
        dto.setConsultant(UserDTO.fromEntity(application.getConsultant()));
        dto.setCoverLetter(application.getCoverLetter());
        dto.setResumeUrl(application.getResumeUrl());
        dto.setStatus(application.getStatus());
        dto.setAppliedDate(application.getAppliedDate());
        dto.setReviewDate(application.getReviewDate());
        dto.setStatusNotes(application.getStatusNotes());
        
        if (application.getPlacement() != null) {
            dto.setPlacement(PlacementDTO.fromEntity(application.getPlacement()));
        }
        
        return dto;
    }
}