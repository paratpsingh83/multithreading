package com.jobportal.service;

import com.jobportal.dto.AuditLogDTO;
import com.jobportal.dto.SystemHealthDTO;
import com.jobportal.dto.UserActivityDTO;

import java.util.List;

public interface AdminService {
    List<AuditLogDTO> getAuditLogs();

    SystemHealthDTO getSystemHealth();

    List<UserActivityDTO> getUserActivity();

    String createBackup();

    void cleanupOldData();

    SystemHealthDTO getSystemMetrics();

    void runDatabaseMaintenance();
    void sendSystemAnnouncement(String title, String message, List<Long> userIds);



//    List<AuditLogDTO> getAuditLogs();
//    SystemHealthDTO getSystemHealth();
//    List<UserActivityDTO> getUserActivity();
//    String createBackup();
//    void cleanupOldData();
//    SystemHealthDTO getSystemMetrics();
//    void runDatabaseMaintenance();
//    void sendSystemAnnouncement(String title, String message, List<Long> userIds);
}