package com.jobportal.serviceImpl;

import com.jobportal.dto.CompanyDTO;
import com.jobportal.dto.CompanyRequestDTO;
import com.jobportal.dto.JobDTO;
import com.jobportal.entity.Company;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.service.CompanyService;
import com.jobportal.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final JobService jobService;

    @Override
    @Transactional
    public CompanyDTO create(CompanyRequestDTO companyRequestDTO) {
        // Validate company name uniqueness
        if (companyRepository.existsByCompanyName(companyRequestDTO.getCompanyName())) {
            throw ExceptionUtils.duplicateResource("Company", "name", companyRequestDTO.getCompanyName());
        }

        // Validate tax ID uniqueness if provided
        if (companyRequestDTO.getTaxId() != null &&
                companyRepository.existsByTaxId(companyRequestDTO.getTaxId())) {
            throw ExceptionUtils.duplicateResource("Company", "tax ID", companyRequestDTO.getTaxId());
        }

        Company company = new Company();
        company.setCompanyName(companyRequestDTO.getCompanyName());
        company.setIndustry(companyRequestDTO.getIndustry());
        company.setSizeRange(companyRequestDTO.getSizeRange());
        company.setWebsite(companyRequestDTO.getWebsite());
        company.setLogo(companyRequestDTO.getLogo());
        company.setDescription(companyRequestDTO.getDescription());
        company.setAddress(companyRequestDTO.getAddress());
        company.setCity(companyRequestDTO.getCity());
        company.setState(companyRequestDTO.getState());
        company.setCountry(companyRequestDTO.getCountry());
        company.setZipCode(companyRequestDTO.getZipCode());
        company.setTaxId(companyRequestDTO.getTaxId());
        company.setStatus(Company.CompanyStatus.ACTIVE);

        Company savedCompany = companyRepository.save(company);
        return CompanyDTO.fromEntity(savedCompany);
    }

    @Override
    public CompanyDTO getById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Company", "id", id));
        return CompanyDTO.fromEntity(company);
    }

    @Override
    public List<CompanyDTO> getAll() {
        return companyRepository.findAll()
                .stream()
                .map(CompanyDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompanyDTO update(Long id, CompanyRequestDTO companyRequestDTO) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Company", "id", id));

        // Validate company name uniqueness if changing
        if (companyRequestDTO.getCompanyName() != null &&
                !companyRequestDTO.getCompanyName().equals(company.getCompanyName())) {
            if (companyRepository.existsByCompanyName(companyRequestDTO.getCompanyName())) {
                throw ExceptionUtils.duplicateResource("Company", "name", companyRequestDTO.getCompanyName());
            }
            company.setCompanyName(companyRequestDTO.getCompanyName());
        }

        // Validate tax ID uniqueness if changing
        if (companyRequestDTO.getTaxId() != null &&
                !companyRequestDTO.getTaxId().equals(company.getTaxId())) {
            if (companyRepository.existsByTaxId(companyRequestDTO.getTaxId())) {
                throw ExceptionUtils.duplicateResource("Company", "tax ID", companyRequestDTO.getTaxId());
            }
            company.setTaxId(companyRequestDTO.getTaxId());
        }

        // Update other fields
        if (companyRequestDTO.getIndustry() != null) {
            company.setIndustry(companyRequestDTO.getIndustry());
        }
        if (companyRequestDTO.getSizeRange() != null) {
            company.setSizeRange(companyRequestDTO.getSizeRange());
        }
        if (companyRequestDTO.getWebsite() != null) {
            company.setWebsite(companyRequestDTO.getWebsite());
        }
        if (companyRequestDTO.getLogo() != null) {
            company.setLogo(companyRequestDTO.getLogo());
        }
        if (companyRequestDTO.getDescription() != null) {
            company.setDescription(companyRequestDTO.getDescription());
        }
        if (companyRequestDTO.getAddress() != null) {
            company.setAddress(companyRequestDTO.getAddress());
        }
        if (companyRequestDTO.getCity() != null) {
            company.setCity(companyRequestDTO.getCity());
        }
        if (companyRequestDTO.getState() != null) {
            company.setState(companyRequestDTO.getState());
        }
        if (companyRequestDTO.getCountry() != null) {
            company.setCountry(companyRequestDTO.getCountry());
        }
        if (companyRequestDTO.getZipCode() != null) {
            company.setZipCode(companyRequestDTO.getZipCode());
        }

        Company updatedCompany = companyRepository.save(company);
        return CompanyDTO.fromEntity(updatedCompany);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Company", "id", id));

        // Check if company has active jobs
        if (!company.getJobs().isEmpty()) {
            throw ExceptionUtils.businessError("Cannot delete company with active jobs");
        }

        // Soft delete by changing status
        company.setStatus(Company.CompanyStatus.INACTIVE);
        companyRepository.save(company);
    }

    @Override
    public List<JobDTO> getCompanyJobs(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Company", "id", companyId));

        return jobService.getJobsByCompany(companyId);
    }

    @Override
    public List<CompanyDTO> getByStatus(String status) {
        try {
            Company.CompanyStatus companyStatus = Company.CompanyStatus.valueOf(status.toUpperCase());
            return companyRepository.findByStatus(companyStatus)
                    .stream()
                    .map(CompanyDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw ExceptionUtils.validationError("Invalid company status: " + status);
        }
    }
}