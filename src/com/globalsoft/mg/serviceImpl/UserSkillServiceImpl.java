package com.jobportal.serviceImpl;

import com.jobportal.dto.SkillDTO;
import com.jobportal.dto.UserSkillDTO;
import com.jobportal.dto.UserSkillRequestDTO;
import com.jobportal.entity.Skill;
import com.jobportal.entity.User;
import com.jobportal.entity.UserSkill;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.repository.SkillRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.repository.UserSkillRepository;
import com.jobportal.service.UserSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSkillServiceImpl implements UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @Override
    @Transactional
    public UserSkillDTO create(UserSkillRequestDTO userSkillRequestDTO) {
        // Get current user (from security context in real app)
        User user = userRepository.findById(1L)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("User", "id", 1L));

        Skill skill = skillRepository.findById(userSkillRequestDTO.getSkillId())
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Skill", "id", userSkillRequestDTO.getSkillId()));

        // Check if user already has this skill
        if (userSkillRepository.findByUserIdAndSkillId(user.getUserId(), skill.getSkillId()).isPresent()) {
            throw ExceptionUtils.duplicateResource("UserSkill", "user and skill",
                    "User: " + user.getUserId() + ", Skill: " + skill.getSkillName());
        }

        // Validate proficiency level
        if (userSkillRequestDTO.getProficiencyLevel() == null) {
            throw ExceptionUtils.validationError("Proficiency level is required");
        }

        // Validate years of experience
        if (userSkillRequestDTO.getYearsOfExperience() != null &&
                userSkillRequestDTO.getYearsOfExperience() < 0) {
            throw ExceptionUtils.validationError("Years of experience cannot be negative");
        }

        UserSkill userSkill = new UserSkill();
        userSkill.setUser(user);
        userSkill.setSkill(skill);
        userSkill.setProficiencyLevel(userSkillRequestDTO.getProficiencyLevel());
        userSkill.setYearsOfExperience(userSkillRequestDTO.getYearsOfExperience());

        UserSkill savedUserSkill = userSkillRepository.save(userSkill);
        return UserSkillDTO.fromEntity(savedUserSkill);
    }

    @Override
    public UserSkillDTO getById(Long id) {
        UserSkill userSkill = userSkillRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("UserSkill", "id", id));
        return UserSkillDTO.fromEntity(userSkill);
    }

    @Override
    public List<UserSkillDTO> getByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw ExceptionUtils.resourceNotFound("User", "id", userId);
        }

        return userSkillRepository.findByUserUserId(userId)
                .stream()
                .map(UserSkillDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserSkillDTO update(Long id, UserSkillRequestDTO userSkillRequestDTO) {
        UserSkill userSkill = userSkillRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("UserSkill", "id", id));

        // Update proficiency level if provided
        if (userSkillRequestDTO.getProficiencyLevel() != null) {
            userSkill.setProficiencyLevel(userSkillRequestDTO.getProficiencyLevel());
        }

        // Update years of experience if provided
        if (userSkillRequestDTO.getYearsOfExperience() != null) {
            if (userSkillRequestDTO.getYearsOfExperience() < 0) {
                throw ExceptionUtils.validationError("Years of experience cannot be negative");
            }
            userSkill.setYearsOfExperience(userSkillRequestDTO.getYearsOfExperience());
        }

        UserSkill updatedUserSkill = userSkillRepository.save(userSkill);
        return UserSkillDTO.fromEntity(updatedUserSkill);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        UserSkill userSkill = userSkillRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("UserSkill", "id", id));
        userSkillRepository.delete(userSkill);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndSkillId(Long userId, Long skillId) {
        UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(userId, skillId)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("UserSkill", "user and skill",
                        "User: " + userId + ", Skill: " + skillId));
        userSkillRepository.delete(userSkill);
    }

    @Override
    public List<SkillDTO> getUserSkills(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw ExceptionUtils.resourceNotFound("User", "id", userId);
        }

        return userSkillRepository.findSkillsByUserId(userId)
                .stream()
                .map(SkillDTO::fromEntity)
                .collect(Collectors.toList());
    }
}