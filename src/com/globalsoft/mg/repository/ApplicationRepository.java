package com.jobportal.repository;

import com.jobportal.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobJobId(Long jobId);

    List<Application> findByConsultantUserId(Long consultantId);

    List<Application> findByStatus(Application.ApplicationStatus status);

    @Query("SELECT a FROM Application a WHERE a.job.jobId = :jobId AND a.consultant.userId = :consultantId")
    Optional<Application> findByJobIdAndConsultantId(@Param("jobId") Long jobId, @Param("consultantId") Long consultantId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'PENDING'")
    Long countPendingApplications();

    @Query("SELECT a FROM Application a WHERE a.job.company.companyId = :companyId")
    List<Application> findByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.jobId = :jobId")
    Long countByJobId(@Param("jobId") Long jobId);

    @Query("SELECT COUNT(a.createdAt) FROM Application a WHERE a.createdAt >= :startDate")
    Long countApplicationsSince(@Param("startDate") LocalDate startDate);


    List<Application> findByJob_JobId(Long jobId);
    List<Application> findByUser_UserId(Long userId);

}