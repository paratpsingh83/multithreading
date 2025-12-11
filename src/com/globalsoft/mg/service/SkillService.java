package com.jobportal.service;

import com.jobportal.dto.SkillDTO;
import com.jobportal.dto.SkillRequestDTO;

import java.util.List;

public interface SkillService {
    SkillDTO create(SkillRequestDTO skillRequestDTO);

    SkillDTO getById(Long id);

    List<SkillDTO> getAll();

    SkillDTO update(Long id, SkillRequestDTO skillRequestDTO);

    void delete(Long id);

    List<String> getCategories();

    List<SkillDTO> searchSkills(String query);
}