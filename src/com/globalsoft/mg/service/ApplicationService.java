package com.jobportal.service;

import com.jobportal.dto.ApplicationDTO;
import com.jobportal.dto.ApplicationRequestDTO;
import com.jobportal.dto.ApplicationStatusDTO;

import java.util.List;

public interface ApplicationService {
    ApplicationDTO create(ApplicationRequestDTO applicationRequestDTO);

    ApplicationDTO getById(Long id);

    List<ApplicationDTO> getAll();

    ApplicationDTO update(Long id, ApplicationRequestDTO applicationRequestDTO);

    void delete(Long id);

    ApplicationDTO updateStatus(Long id, ApplicationStatusDTO statusDTO);

    List<ApplicationDTO> getByJobId(Long jobId);

    List<ApplicationDTO> getByUserId(Long userId);

    List<ApplicationDTO> getByStatus(String status);

    Long getPendingApplicationCount();
}