package com.jobportal.repository;

import com.jobportal.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    List<UserSkill> findByUserUserId(Long userId);

    List<UserSkill> findBySkillSkillId(Long skillId);

    @Query("SELECT us FROM UserSkill us WHERE us.user.userId = :userId AND us.skill.skillId = :skillId")
    Optional<UserSkill> findByUserIdAndSkillId(@Param("userId") Long userId, @Param("skillId") Long skillId);

    @Query("SELECT us.skill FROM UserSkill us WHERE us.user.userId = :userId")
    List<com.jobportal.entity.Skill> findSkillsByUserId(@Param("userId") Long userId);

    @Query("SELECT us FROM UserSkill us WHERE us.skill.skillName = :skillName")
    List<UserSkill> findBySkillName(@Param("skillName") String skillName);
}