package com.jobportal.repository;

import com.jobportal.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findBySkillName(String skillName);

    List<Skill> findBySkillCategory(String category);

    @Query("SELECT DISTINCT s.skillCategory FROM Skill s WHERE s.skillCategory IS NOT NULL")
    List<String> findAllCategories();

    @Query("SELECT s FROM Skill s WHERE LOWER(s.skillName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Skill> findBySkillNameContaining(String name);
}