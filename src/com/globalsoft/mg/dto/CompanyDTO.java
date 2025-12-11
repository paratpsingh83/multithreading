package com.jobportal.dto;

import com.jobportal.entity.Company;
import lombok.Data;

@Data
public class CompanyDTO {
    private Long companyId;
    private String companyName;
    private String industry;
    private String sizeRange;
    private String website;
    private String logo;
    private String description;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String taxId;
    private Company.CompanyStatus status;
    
    public static CompanyDTO fromEntity(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setCompanyId(company.getCompanyId());
        dto.setCompanyName(company.getCompanyName());
        dto.setIndustry(company.getIndustry());
        dto.setSizeRange(company.getSizeRange());
        dto.setWebsite(company.getWebsite());
        dto.setLogo(company.getLogo());
        dto.setDescription(company.getDescription());
        dto.setAddress(company.getAddress());
        dto.setCity(company.getCity());
        dto.setState(company.getState());
        dto.setCountry(company.getCountry());
        dto.setZipCode(company.getZipCode());
        dto.setTaxId(company.getTaxId());
        dto.setStatus(company.getStatus());
        return dto;
    }
}