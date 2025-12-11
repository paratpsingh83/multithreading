package com.jobportal.dto;

import com.jobportal.entity.Skill;
import lombok.Data;

@Data
public class SkillDTO {
    private Long skillId;
    private String skillName;
    private String skillCategory;
    private String description;
    
    public static SkillDTO fromEntity(Skill skill) {
        SkillDTO dto = new SkillDTO();
        dto.setSkillId(skill.getSkillId());
        dto.setSkillName(skill.getSkillName());
        dto.setSkillCategory(skill.getSkillCategory());
        dto.setDescription(skill.getDescription());
        return dto;
    }
}