package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyRequestDTO {
    @NotBlank(message = "Company name is required")
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
}