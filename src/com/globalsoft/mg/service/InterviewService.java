package com.jobportal.service;

import com.jobportal.dto.InterviewDTO;
import com.jobportal.dto.InterviewRequestDTO;
import com.jobportal.dto.InterviewStatusDTO;

import java.util.List;

public interface InterviewService {
    InterviewDTO create(InterviewRequestDTO interviewRequestDTO);

    InterviewDTO getById(Long id);

    List<InterviewDTO> getAll();

    InterviewDTO update(Long id, InterviewRequestDTO interviewRequestDTO);

    void delete(Long id);

    InterviewDTO updateStatus(Long id, InterviewStatusDTO statusDTO);

    List<InterviewDTO> getByApplicationId(Long applicationId);

    List<InterviewDTO> getUpcomingInterviews();
}