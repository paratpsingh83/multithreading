package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillRequestDTO {
    @NotBlank(message = "Skill name is required")
    private String skillName;
    
    private String skillCategory;
    private String description;
}