//package com.jobportal.dto;
//
//import com.jobportal.entity.UserSkill;
//import lombok.Data;
//
//@Data
//public class UserSkillDTO {
//    private Long userSkillId;
//    private SkillDTO skill;
//    private UserSkill.ProficiencyLevel proficiencyLevel;
//    private Integer yearsOfExperience;
//
//    public static UserSkillDTO fromEntity(UserSkill userSkill) {
//        UserSkillDTO dto = new UserSkillDTO();
//        dto.setUserSkillId(userSkill.getUserSkillId());
//        dto.setSkill(SkillDTO.fromEntity(userSkill.getSkill()));
//        dto.setProficiencyLevel(userSkill.getProficiencyLevel());
//        dto.setYearsOfExperience(userSkill.getYearsOfExperience());
//        return dto;
//    }
//}

package com.jobportal.dto;

import com.jobportal.entity.UserSkill;
import com.jobportal.enums.ProficiencyLevel; // Import from common enum package
import lombok.Data;

@Data
public class UserSkillDTO {
    private Long userSkillId;
    private SkillDTO skill;
    private ProficiencyLevel proficiencyLevel; // Use common enum
    private Integer yearsOfExperience;

    public static UserSkillDTO fromEntity(UserSkill userSkill) {
        UserSkillDTO dto = new UserSkillDTO();
        dto.setUserSkillId(userSkill.getUserSkillId());
        dto.setSkill(SkillDTO.fromEntity(userSkill.getSkill()));
        dto.setProficiencyLevel(userSkill.getProficiencyLevel());
        dto.setYearsOfExperience(userSkill.getYearsOfExperience());
        return dto;
    }
}