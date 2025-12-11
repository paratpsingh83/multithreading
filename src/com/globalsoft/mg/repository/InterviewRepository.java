package com.jobportal.repository;

import com.jobportal.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByApplicationApplicationId(Long applicationId);

    List<Interview> findByStatus(Interview.InterviewStatus status);

    @Query("SELECT i FROM Interview i WHERE i.application.consultant.userId = :consultantId")
    List<Interview> findByConsultantId(@Param("consultantId") Long consultantId);

    @Query("SELECT i FROM Interview i WHERE i.interviewDate BETWEEN :startDate AND :endDate")
    List<Interview> findByInterviewDateRange(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(i) FROM Interview i WHERE i.status = 'SCHEDULED' AND i.interviewDate >= CURRENT_TIMESTAMP")
    Long countUpcomingInterviews();
}