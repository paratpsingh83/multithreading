package com.jobportal.service;

import com.jobportal.dto.CompanyDTO;
import com.jobportal.dto.CompanyRequestDTO;
import com.jobportal.dto.JobDTO;

import java.util.List;

public interface CompanyService {
    CompanyDTO create(CompanyRequestDTO companyRequestDTO);

    CompanyDTO getById(Long id);

    List<CompanyDTO> getAll();

    CompanyDTO update(Long id, CompanyRequestDTO companyRequestDTO);

    void delete(Long id);

    List<JobDTO> getCompanyJobs(Long companyId);

    List<CompanyDTO> getByStatus(String status);
}