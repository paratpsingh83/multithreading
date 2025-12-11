package com.jobportal.repository;

import com.jobportal.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(Job.JobStatus status);

    List<Job> findByCompanyCompanyId(Long companyId);

    List<Job> findByJobType(Job.JobType jobType);

    List<Job> findByLocationContainingIgnoreCase(String location);

    List<Job> findByRemoteOption(Boolean remoteOption);

    @Query("SELECT j FROM Job j WHERE j.status = 'OPEN' ORDER BY j.createdAt DESC")
    List<Job> findOpenJobs();



    @Query("SELECT j FROM Job j WHERE " +
            "(:keyword IS NULL OR LOWER(j.jobTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.jobDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:jobType IS NULL OR j.jobType = :jobType) AND " +
            "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:remoteOption IS NULL OR j.remoteOption = :remoteOption)")
    Page<Job> searchJobs(@Param("keyword") String keyword,
                         @Param("jobType") Job.JobType jobType,
                         @Param("location") String location,
                         @Param("remoteOption") Boolean remoteOption,
                         Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.createdBy.userId = :userId")
    List<Job> findByCreatedBy(Long userId);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.status = 'OPEN'")
    Long countOpenJobs();
}