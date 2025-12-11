package com.jobportal.dto;

import com.jobportal.entity.Interview;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InterviewDTO {
    private Long interviewId;
    private ApplicationDTO application;
    private LocalDateTime interviewDate;
    private Interview.InterviewType interviewType;
    private Interview.InterviewStatus status;
    private String feedback;
    private Integer rating;
    private List<String> interviewers;
    
    public static InterviewDTO fromEntity(Interview interview) {
        InterviewDTO dto = new InterviewDTO();
        dto.setInterviewId(interview.getInterviewId());
        dto.setApplication(ApplicationDTO.fromEntity(interview.getApplication()));
        dto.setInterviewDate(interview.getInterviewDate());
        dto.setInterviewType(interview.getInterviewType());
        dto.setStatus(interview.getStatus());
        dto.setFeedback(interview.getFeedback());
        dto.setRating(interview.getRating());
        return dto;
    }
}