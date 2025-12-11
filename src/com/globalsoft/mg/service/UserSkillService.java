package com.jobportal.service;

import com.jobportal.dto.SkillDTO;
import com.jobportal.dto.UserSkillDTO;
import com.jobportal.dto.UserSkillRequestDTO;

import java.util.List;

public interface UserSkillService {

    UserSkillDTO create(UserSkillRequestDTO userSkillRequestDTO);

    UserSkillDTO getById(Long id);

    List<UserSkillDTO> getByUserId(Long userId);

    UserSkillDTO update(Long id, UserSkillRequestDTO userSkillRequestDTO);

    void delete(Long id);

    void deleteByUserIdAndSkillId(Long userId, Long skillId);

    List<SkillDTO> getUserSkills(Long userId);
}