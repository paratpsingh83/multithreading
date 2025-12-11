package com.jobportal.serviceImpl;

import com.jobportal.dto.SkillDTO;
import com.jobportal.dto.SkillRequestDTO;
import com.jobportal.entity.Skill;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.repository.SkillRepository;
import com.jobportal.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    @Transactional
    public SkillDTO create(SkillRequestDTO skillRequestDTO) {
        // Validate skill name
        if (skillRequestDTO.getSkillName() == null || skillRequestDTO.getSkillName().trim().isEmpty()) {
            throw ExceptionUtils.validationError("Skill name is required");
        }

        // Check if skill already exists (case insensitive)
        String skillName = skillRequestDTO.getSkillName().trim();
        skillRepository.findBySkillName(skillName)
                .ifPresent(existingSkill -> {
                    throw ExceptionUtils.duplicateResource("Skill", "name", skillName);
                });

        Skill skill = new Skill();
        skill.setSkillName(skillName);
        skill.setSkillCategory(skillRequestDTO.getSkillCategory());
        skill.setDescription(skillRequestDTO.getDescription());

        Skill savedSkill = skillRepository.save(skill);
        return SkillDTO.fromEntity(savedSkill);
    }

    @Override
    public SkillDTO getById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Skill", "id", id));
        return SkillDTO.fromEntity(skill);
    }

    @Override
    public List<SkillDTO> getAll() {
        return skillRepository.findAll()
                .stream()
                .map(SkillDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SkillDTO update(Long id, SkillRequestDTO skillRequestDTO) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Skill", "id", id));

        // Validate skill name if changing
        if (skillRequestDTO.getSkillName() != null &&
                !skillRequestDTO.getSkillName().trim().equals(skill.getSkillName())) {

            String newSkillName = skillRequestDTO.getSkillName().trim();
            if (newSkillName.isEmpty()) {
                throw ExceptionUtils.validationError("Skill name cannot be empty");
            }

            // Check for duplicate name
            skillRepository.findBySkillName(newSkillName)
                    .ifPresent(existingSkill -> {
                        throw ExceptionUtils.duplicateResource("Skill", "name", newSkillName);
                    });

            skill.setSkillName(newSkillName);
        }

        // Update other fields
        if (skillRequestDTO.getSkillCategory() != null) {
            skill.setSkillCategory(skillRequestDTO.getSkillCategory());
        }
        if (skillRequestDTO.getDescription() != null) {
            skill.setDescription(skillRequestDTO.getDescription());
        }

        Skill updatedSkill = skillRepository.save(skill);
        return SkillDTO.fromEntity(updatedSkill);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Skill", "id", id));

        // Check if skill is being used by any user
        if (!skill.getUserSkills().isEmpty()) {
            throw ExceptionUtils.businessError(
                    String.format("Cannot delete skill '%s' that is associated with %d users",
                            skill.getSkillName(), skill.getUserSkills().size())
            );
        }

        skillRepository.delete(skill);
    }

    @Override
    public List<String> getCategories() {
        return skillRepository.findAllCategories();
    }

    @Override
    public List<SkillDTO> searchSkills(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw ExceptionUtils.validationError("Search query is required");
        }

        String searchQuery = query.trim();
        if (searchQuery.length() < 2) {
            throw ExceptionUtils.validationError("Search query must be at least 2 characters long");
        }

        return skillRepository.findBySkillNameContaining(searchQuery)
                .stream()
                .map(SkillDTO::fromEntity)
                .collect(Collectors.toList());
    }
}