//package com.jobportal.dto;
//
//import jakarta.validation.constraints.*;
//
//import lombok.Data;
//
//@Data
//public class UserSkillRequestDTO {
//
//    @NotNull(message = "Skill ID is required")
//    private Long skillId;
//
//    @NotNull(message = "Proficiency level is required")
//    private ProficiencyLevel proficiencyLevel;
//
//    @NotNull(message = "Years of experience is required")
//    @Min(value = 0, message = "Years of experience cannot be negative")
//    @Max(value = 50, message = "Years of experience cannot exceed 50 years")
//    private Integer yearsOfExperience;
//
//    public enum ProficiencyLevel {
//        BEGINNER, INTERMEDIATE, EXPERT
//    }
//}
package com.jobportal.dto;

import com.jobportal.enums.ProficiencyLevel; // Import common enum
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserSkillRequestDTO {

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Proficiency level is required")
    private ProficiencyLevel proficiencyLevel; // Use common enum

    @NotNull(message = "Years of experience is required")
    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 50, message = "Years of experience cannot exceed 50 years")
    private Integer yearsOfExperience;
}
