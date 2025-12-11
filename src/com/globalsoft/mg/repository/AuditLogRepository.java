package com.jobportal.repository;

import com.jobportal.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserUserId(Long userId);

    List<AuditLog> findByEntityType(String entityType);

    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :startDate AND :endDate")
    List<AuditLog> findByTimestampBetween(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.timestamp DESC")
    List<AuditLog> findByEntityTypeAndEntityId(@Param("entityType") String entityType,
                                               @Param("entityId") Long entityId);

    @Query("SELECT a FROM AuditLog a WHERE a.action = :action")
    List<AuditLog> findByAction(@Param("action") String action);
}