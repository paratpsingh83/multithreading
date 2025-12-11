//package com.jobportal.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//@Entity
//@Table(name = "user_skills")
//@Data
//public class UserSkill {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long userSkillId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "skill_id", nullable = false)
//    private Skill skill;
//
//    @Enumerated(EnumType.STRING)
//    private ProficiencyLevel proficiencyLevel;
//
//    private Integer yearsOfExperience;
//
//    public enum ProficiencyLevel {
//        BEGINNER, INTERMEDIATE, EXPERT
//    }
//}

package com.jobportal.entity;

import com.jobportal.enums.ProficiencyLevel; // Import common enum
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_skills")
@Data
public class UserSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userSkillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    private ProficiencyLevel proficiencyLevel; // Use common enum

    private Integer yearsOfExperience;

}