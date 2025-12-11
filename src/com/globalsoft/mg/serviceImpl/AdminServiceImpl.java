////package com.jobportal.serviceImpl;
////
////import com.jobportal.dto.AuditLogDTO;
////import com.jobportal.dto.SystemHealthDTO;
////import com.jobportal.dto.UserActivityDTO;
////import com.jobportal.entity.AuditLog;
////import com.jobportal.entity.User;
////import com.jobportal.exception.ExceptionUtils;
////import com.jobportal.repository.*;
////import com.jobportal.service.AdminService;
////import lombok.RequiredArgsConstructor;
////import org.springframework.stereotype.Service;
////import org.springframework.transaction.annotation.Transactional;
////
////import java.lang.management.ManagementFactory;
////import java.lang.management.MemoryMXBean;
////import java.lang.management.OperatingSystemMXBean;
////import java.time.LocalDateTime;
////import java.time.format.DateTimeFormatter;
////import java.util.HashMap;
////import java.util.List;
////import java.util.Map;
////import java.util.stream.Collectors;
////
////@Service
////@RequiredArgsConstructor
////public class AdminServiceImpl implements AdminService {
////
////    private final AuditLogRepository auditLogRepository;
////    private final UserRepository userRepository;
////    private final PlacementRepository placementRepository;
////    private final InvoiceRepository invoiceRepository;
////    private final PaymentRepository paymentRepository;
////    private final ApplicationRepository applicationRepository;
////
////    @Override
////    public List<AuditLogDTO> getAuditLogs() {
////        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
////        LocalDateTime endDate = LocalDateTime.now();
////
////        return auditLogRepository.findByTimestampBetween(startDate, endDate)
////                .stream()
////                .map(AuditLogDTO::fromEntity)
////                .collect(Collectors.toList());
////    }
////
////
////
////
////    public Map<String, Object> getSystemStatistics() {
////        Map<String, Object> stats = new HashMap<>();
////
////        // User statistics
////        stats.put("totalUsers", userRepository.count());
////        stats.put("activeUsers", userRepository.countByIsActive(true));
////
////        // Fixed: Calculate start date and pass as parameter
////        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
////        stats.put("newUsersThisMonth", userRepository.countNewConsultantsThisMonth(startDate));
////
////
////        // Placement statistics
////        stats.put("totalPlacements", placementRepository.count());
////        stats.put("activePlacements", placementRepository.countActivePlacements());
////        stats.put("placementSuccessRate", calculatePlacementSuccessRate());
////
////        // Financial statistics
////        Double totalRevenue = paymentRepository.sumPaymentsSince(LocalDateTime.now().minusMonths(1).toLocalDate());
////        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
////
////        Double outstandingRevenue = invoiceRepository.sumOutstandingAmount();
////        stats.put("outstandingRevenue", outstandingRevenue != null ? outstandingRevenue : 0.0);
////
////        // System statistics
////        stats.put("auditLogsCount", auditLogRepository.count());
////        stats.put("systemUptime", getSystemUptime());
////
////        return stats;
////    }
////
////
////
////
////
////    @Override
////    public SystemHealthDTO getSystemHealth() {
////        SystemHealthDTO health = new SystemHealthDTO();
////
////        // Database connectivity check
////        try {
////            userRepository.count();
////            health.setDatabaseStatus("HEALTHY");
////        } catch (Exception e) {
////            health.setDatabaseStatus("UNHEALTHY - " + e.getMessage());
////        }
////
////        // User statistics
////        health.setTotalUsers(userRepository.count());
////        health.setActiveUsers(userRepository.countByIsActive(true));
////
////        // Placement statistics
////        health.setActivePlacements(placementRepository.countActivePlacements());
////
////        // Financial statistics
////        Double outstandingRevenue = invoiceRepository.sumOutstandingAmount();
////        health.setOutstandingRevenue(outstandingRevenue != null ? outstandingRevenue : 0.0);
////
////        // System information
////        health.setServerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
////        health.setUptime(getSystemUptime());
////
////        // Memory usage
////        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
////        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
////        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
////        double memoryUsage = maxMemory > 0 ? (double) usedMemory / maxMemory * 100 : 0;
////        health.setMemoryUsage(Math.round(memoryUsage * 100.0) / 100.0);
////
////        // CPU usage
////        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
////        double systemLoad = osBean.getSystemLoadAverage();
////        health.setCpuUsage(systemLoad >= 0 ? Math.round(systemLoad * 100.0) / 100.0 : 0.0);
////
////        // Overall status
////        if ("HEALTHY".equals(health.getDatabaseStatus()) && memoryUsage < 90 && systemLoad < 80) {
////            health.setStatus("OPERATIONAL");
////        } else if (memoryUsage >= 90 || systemLoad >= 80) {
////            health.setStatus("DEGRADED");
////        } else {
////            health.setStatus("UNHEALTHY");
////        }
////
////        return health;
////    }
////
////    @Override
////    public List<UserActivityDTO> getUserActivity() {
////        LocalDateTime startTime = LocalDateTime.now().minusHours(24);
////
////        return auditLogRepository.findByTimestampBetween(startTime, LocalDateTime.now())
////                .stream()
////                .collect(Collectors.groupingBy(AuditLog::getUser))
////                .entrySet()
////                .stream()
////                .map(entry -> {
////                    User user = entry.getKey();
////                    List<AuditLog> userLogs = entry.getValue();
////
////                    UserActivityDTO activity = new UserActivityDTO();
////                    activity.setUserId(user.getUserId());
////                    activity.setUsername(user.getUsername());
////                    activity.setUserType(user.getUserType().toString());
////                    activity.setLastAction(userLogs.get(0).getAction());
////                    activity.setLastActive(userLogs.get(0).getTimestamp());
////                    activity.setIpAddress(userLogs.get(0).getIpAddress());
////                    activity.setIsOnline(isUserOnline(user.getUserId()));
////
////                    return activity;
////                })
////                .sorted((a1, a2) -> a2.getLastActive().compareTo(a1.getLastActive()))
////                .collect(Collectors.toList());
////    }
////
////    @Override
////    @Transactional
////    public String createBackup() {
////        SystemHealthDTO health = getSystemHealth();
////        if (!"OPERATIONAL".equals(health.getStatus())) {
////            throw ExceptionUtils.businessError("Cannot create backup when system status is: " + health.getStatus());
////        }
////
////        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
////        String backupFileName = "backup_" + timestamp + ".sql";
////
////        try {
////            Thread.sleep(2000);
////
////            AuditLog backupLog = new AuditLog();
////            backupLog.setAction("SYSTEM_BACKUP_CREATED");
////            backupLog.setEntityType("SYSTEM");
////            backupLog.setEntityId(null);
////            backupLog.setNewValues("{\"backupFile\": \"" + backupFileName + "\"}");
////            backupLog.setTimestamp(LocalDateTime.now());
////            auditLogRepository.save(backupLog);
////
////            return backupFileName;
////        } catch (InterruptedException e) {
////            Thread.currentThread().interrupt();
////            throw ExceptionUtils.businessError("Backup process interrupted");
////        }
////    }
////
////    @Override
////    @Transactional
////    public void cleanupOldData() {
////        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(6);
////
////        List<AuditLog> oldLogs = auditLogRepository.findByTimestampBetween(
////                LocalDateTime.of(2000, 1, 1, 0, 0),
////                cutoffDate
////        );
////
////        if (!oldLogs.isEmpty()) {
////            auditLogRepository.deleteAll(oldLogs);
////
////            AuditLog cleanupLog = new AuditLog();
////            cleanupLog.setAction("SYSTEM_DATA_CLEANUP");
////            cleanupLog.setEntityType("SYSTEM");
////            cleanupLog.setEntityId(null);
////            cleanupLog.setNewValues("{\"cleanedRecords\": " + oldLogs.size() + ", \"cutoffDate\": \"" + cutoffDate + "\"}");
////            cleanupLog.setTimestamp(LocalDateTime.now());
////            auditLogRepository.save(cleanupLog);
////        }
////    }
////
////    @Override
////    public SystemHealthDTO getSystemMetrics() {
////        SystemHealthDTO health = getSystemHealth();
////
////        health.setTotalPlacements(placementRepository.count());
////        health.setPendingInvoices(invoiceRepository.countPendingReview() + invoiceRepository.countAwaitingApproval());
////
////        health.setDatabaseConnections(25L);
////        health.setActiveSessions(18L);
////
////        return health;
////    }
////
////    @Override
////    @Transactional
////    public void runDatabaseMaintenance() {
////        try {
////            Thread.sleep(5000);
////
////            AuditLog maintenanceLog = new AuditLog();
////            maintenanceLog.setAction("SYSTEM_DATABASE_MAINTENANCE");
////            maintenanceLog.setEntityType("SYSTEM");
////            maintenanceLog.setEntityId(null);
////            maintenanceLog.setNewValues("{\"maintenanceCompleted\": true, \"timestamp\": \"" + LocalDateTime.now() + "\"}");
////            maintenanceLog.setTimestamp(LocalDateTime.now());
////            auditLogRepository.save(maintenanceLog);
////
////        } catch (InterruptedException e) {
////            Thread.currentThread().interrupt();
////            throw ExceptionUtils.businessError("Database maintenance interrupted");
////        }
////    }
////
////    @Override
////    @Transactional
////    public void sendSystemAnnouncement(String title, String message, List<Long> userIds) {
////        if (title == null || title.trim().isEmpty()) {
////            throw ExceptionUtils.validationError("Announcement title is required");
////        }
////        if (message == null || message.trim().isEmpty()) {
////            throw ExceptionUtils.validationError("Announcement message is required");
////        }
////
////        List<User> targetUsers;
////        if (userIds == null || userIds.isEmpty()) {
////            targetUsers = userRepository.findByIsActive(true);
////        } else {
////            targetUsers = userRepository.findAllById(userIds);
////            if (targetUsers.size() != userIds.size()) {
////                throw ExceptionUtils.resourceNotFound("User", "id", userIds.toString());
////            }
////        }
////
////        AuditLog announcementLog = new AuditLog();
////        announcementLog.setAction("SYSTEM_ANNOUNCEMENT_SENT");
////        announcementLog.setEntityType("SYSTEM");
////        announcementLog.setEntityId(null);
////        announcementLog.setNewValues("{\"title\": \"" + title + "\", \"message\": \"" + message +
////                "\", \"recipients\": " + targetUsers.size() + "}");
////        announcementLog.setTimestamp(LocalDateTime.now());
////        auditLogRepository.save(announcementLog);
////    }
////
////    private String getSystemUptime() {
////        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
////        long days = uptime / (1000 * 60 * 60 * 24);
////        long hours = (uptime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
////        long minutes = (uptime % (1000 * 60 * 60)) / (1000 * 60);
////
////        return String.format("%d days, %d hours, %d minutes", days, hours, minutes);
////    }
////
////    private boolean isUserOnline(Long userId) {
////        LocalDateTime last15Minutes = LocalDateTime.now().minusMinutes(15);
////        return auditLogRepository.findByTimestampBetween(last15Minutes, LocalDateTime.now())
////                .stream()
////                .anyMatch(log -> log.getUser() != null && log.getUser().getUserId().equals(userId));
////    }
////
////
////    private double calculatePlacementSuccessRate() {
////        long totalApplications = applicationRepository.count();
////        long successfulPlacements = placementRepository.count();
////
////        if (totalApplications == 0) {
////            return 0.0;
////        }
////
////        return (double) successfulPlacements / totalApplications * 100;
////    }
////}
//
//
//package com.jobportal.serviceImpl;
//
//import com.jobportal.dto.AuditLogDTO;
//import com.jobportal.dto.SystemHealthDTO;
//import com.jobportal.dto.UserActivityDTO;
//import com.jobportal.entity.AuditLog;
//import com.jobportal.entity.User;
//import com.jobportal.exception.ExceptionUtils;
//import com.jobportal.repository.*;
//import com.jobportal.service.AdminService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.lang.management.ManagementFactory;
//import java.lang.management.MemoryMXBean;
//import java.lang.management.OperatingSystemMXBean;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AdminServiceImpl implements AdminService {
//
//    private final AuditLogRepository auditLogRepository;
//    private final UserRepository userRepository;
//    private final PlacementRepository placementRepository;
//    private final InvoiceRepository invoiceRepository;
//    private final PaymentRepository paymentRepository;
//    private final ApplicationRepository applicationRepository;
//
//    @Override
//    public List<AuditLogDTO> getAuditLogs() {
//        log.debug("Fetching audit logs for last 30 days");
//
//        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
//        LocalDateTime endDate = LocalDateTime.now();
//
//        return auditLogRepository.findByTimestampBetween(startDate, endDate)
//                .stream()
//                .map(AuditLogDTO::fromEntity)
//                .collect(Collectors.toList());
//    }
//
//    public Map<String, Object> getSystemStatistics() {
//        log.debug("Generating system statistics");
//
//        Map<String, Object> stats = new HashMap<>();
//        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
//
//        // User statistics
//        stats.put("totalUsers", userRepository.count());
//        stats.put("activeUsers", userRepository.countByIsActive(true));
//        stats.put("newUsersThisMonth", userRepository.countUsersCreatedAfter(thirtyDaysAgo));
//        stats.put("newConsultantsThisMonth", userRepository.countConsultantsCreatedAfter(thirtyDaysAgo));
//
//        // Placement statistics
//        stats.put("totalPlacements", placementRepository.count());
//        stats.put("activePlacements", placementRepository.countActivePlacements());
//        stats.put("placementSuccessRate", calculatePlacementSuccessRate());
//
//        // Financial statistics
//        Double totalRevenue = paymentRepository.sumPaymentsSince(LocalDateTime.now().minusMonths(1).toLocalDate());
//        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
//
//        Double outstandingRevenue = invoiceRepository.sumOutstandingAmount();
//        stats.put("outstandingRevenue", outstandingRevenue != null ? outstandingRevenue : 0.0);
//
//        // System statistics
//        stats.put("auditLogsCount", auditLogRepository.count());
//        stats.put("systemUptime", getSystemUptime());
//
//        return stats;
//    }
//
//    @Override
//    public SystemHealthDTO getSystemHealth() {
//        log.debug("Checking system health");
//
//        SystemHealthDTO health = new SystemHealthDTO();
//
//        // Database connectivity check
//        try {
//            userRepository.count();
//            health.setDatabaseStatus("HEALTHY");
//            log.debug("Database connectivity: HEALTHY");
//        } catch (Exception e) {
//            health.setDatabaseStatus("UNHEALTHY - " + e.getMessage());
//            log.error("Database connectivity check failed: {}", e.getMessage());
//        }
//
//        // User statistics
//        health.setTotalUsers(userRepository.count());
//        health.setActiveUsers(userRepository.countByIsActive(true));
//
//        // Placement statistics
//        health.setActivePlacements(placementRepository.countActivePlacements());
//
//        // Financial statistics
//        Double outstandingRevenue = invoiceRepository.sumOutstandingAmount();
//        health.setOutstandingRevenue(outstandingRevenue != null ? outstandingRevenue : 0.0);
//
//        // System information
//        health.setServerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        health.setUptime(getSystemUptime());
//
//        // Memory usage
//        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
//        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
//        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
//        double memoryUsage = maxMemory > 0 ? (double) usedMemory / maxMemory * 100 : 0;
//        health.setMemoryUsage(Math.round(memoryUsage * 100.0) / 100.0);
//
//        // CPU usage
//        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
//        double systemLoad = osBean.getSystemLoadAverage();
//        health.setCpuUsage(systemLoad >= 0 ? Math.round(systemLoad * 100.0) / 100.0 : 0.0);
//
//        // Overall status determination
//        String overallStatus = determineOverallStatus(health.getDatabaseStatus(), memoryUsage, systemLoad);
//        health.setStatus(overallStatus);
//
//        log.debug("System health check completed. Status: {}", overallStatus);
//        return health;
//    }
//
//    @Override
//    public List<UserActivityDTO> getUserActivity() {
//        log.debug("Fetching user activity for last 24 hours");
//
//        LocalDateTime startTime = LocalDateTime.now().minusHours(24);
//
//        return auditLogRepository.findByTimestampBetween(startTime, LocalDateTime.now())
//                .stream()
//                .filter(log -> log.getUser() != null) // Only logs with users
//                .collect(Collectors.groupingBy(AuditLog::getUser))
//                .entrySet()
//                .stream()
//                .map(entry -> {
//                    User user = entry.getKey();
//                    List<AuditLog> userLogs = entry.getValue();
//
//                    UserActivityDTO activity = new UserActivityDTO();
//                    activity.setUserId(user.getUserId());
//                    activity.setUsername(user.getUsername());
//                    activity.setUserType(user.getUserType().toString());
//                    activity.setLastAction(userLogs.get(0).getAction());
//                    activity.setLastActive(userLogs.get(0).getTimestamp());
//                    activity.setIpAddress(userLogs.get(0).getIpAddress());
//                    activity.setIsOnline(isUserOnline(user.getUserId()));
//
//                    return activity;
//                })
//                .sorted((a1, a2) -> a2.getLastActive().compareTo(a1.getLastActive()))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public String createBackup() {
//        log.info("Initiating system backup");
//
//        SystemHealthDTO health = getSystemHealth();
//        if (!"OPERATIONAL".equals(health.getStatus())) {
//            log.warn("Backup aborted due to system status: {}", health.getStatus());
//            throw ExceptionUtils.businessError("Cannot create backup when system status is: " + health.getStatus());
//        }
//
//        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
//        String backupFileName = "backup_" + timestamp + ".sql";
//
//        try {
//            log.debug("Simulating backup process for file: {}", backupFileName);
//            Thread.sleep(2000); // Simulate backup time
//
//            // Log backup operation
//            AuditLog backupLog = new AuditLog();
//            backupLog.setAction("SYSTEM_BACKUP_CREATED");
//            backupLog.setEntityType("SYSTEM");
//            backupLog.setEntityId(null);
//            backupLog.setNewValues("{\"backupFile\": \"" + backupFileName + "\"}");
//            backupLog.setTimestamp(LocalDateTime.now());
//            auditLogRepository.save(backupLog);
//
//            log.info("Backup created successfully: {}", backupFileName);
//            return backupFileName;
//        } catch (InterruptedException e) {
//            log.error("Backup process interrupted", e);
//            Thread.currentThread().interrupt();
//            throw ExceptionUtils.businessError("Backup process interrupted");
//        }
//    }
//
//    @Override
//    @Transactional
//    public void cleanupOldData() {
//        log.info("Initiating old data cleanup");
//
//        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(6);
//
//        List<AuditLog> oldLogs = auditLogRepository.findByTimestampBetween(
//                LocalDateTime.of(2000, 1, 1, 0, 0),
//                cutoffDate
//        );
//
//        if (!oldLogs.isEmpty()) {
//            log.debug("Cleaning up {} old audit logs", oldLogs.size());
//            auditLogRepository.deleteAll(oldLogs);
//
//            // Log cleanup operation
//            AuditLog cleanupLog = new AuditLog();
//            cleanupLog.setAction("SYSTEM_DATA_CLEANUP");
//            cleanupLog.setEntityType("SYSTEM");
//            cleanupLog.setEntityId(null);
//            cleanupLog.setNewValues("{\"cleanedRecords\": " + oldLogs.size() + ", \"cutoffDate\": \"" + cutoffDate + "\"}");
//            cleanupLog.setTimestamp(LocalDateTime.now());
//            auditLogRepository.save(cleanupLog);
//
//            log.info("Successfully cleaned up {} old records", oldLogs.size());
//        } else {
//            log.debug("No old records found for cleanup");
//        }
//    }
//
//    @Override
//    public SystemHealthDTO getSystemMetrics() {
//        log.debug("Fetching detailed system metrics");
//
//        SystemHealthDTO health = getSystemHealth();
//
//        // Additional metrics
//        health.setTotalPlacements(placementRepository.count());
//        health.setPendingInvoices(invoiceRepository.countPendingReview() + invoiceRepository.countAwaitingApproval());
//
//        // System metrics (simplified - in production, use actual monitoring)
//        health.setDatabaseConnections(25L);
//        health.setActiveSessions(18L);
//
//        return health;
//    }
//
//    @Override
//    @Transactional
//    public void runDatabaseMaintenance() {
//        log.info("Starting database maintenance");
//
//        try {
//            // Simulate maintenance tasks
//            Thread.sleep(5000);
//
//            // Log maintenance operation
//            AuditLog maintenanceLog = new AuditLog();
//            maintenanceLog.setAction("SYSTEM_DATABASE_MAINTENANCE");
//            maintenanceLog.setEntityType("SYSTEM");
//            maintenanceLog.setEntityId(null);
//            maintenanceLog.setNewValues("{\"maintenanceCompleted\": true, \"timestamp\": \"" + LocalDateTime.now() + "\"}");
//            maintenanceLog.setTimestamp(LocalDateTime.now());
//            auditLogRepository.save(maintenanceLog);
//
//            log.info("Database maintenance completed successfully");
//        } catch (InterruptedException e) {
//            log.error("Database maintenance interrupted", e);
//            Thread.currentThread().interrupt();
//            throw ExceptionUtils.businessError("Database maintenance interrupted");
//        }
//    }
//
//    @Override
//    @Transactional
//    public void sendSystemAnnouncement(String title, String message, List<Long> userIds) {
//        log.info("Sending system announcement to {} users", userIds != null ? userIds.size() : "all");
//
//        if (title == null || title.trim().isEmpty()) {
//            throw ExceptionUtils.validationError("Announcement title is required");
//        }
//        if (message == null || message.trim().isEmpty()) {
//            throw ExceptionUtils.validationError("Announcement message is required");
//        }
//
//        List<User> targetUsers;
//        if (userIds == null || userIds.isEmpty()) {
//            targetUsers = userRepository.findByIsActive(true);
//            log.debug("Sending announcement to all active users: {}", targetUsers.size());
//        } else {
//            targetUsers = userRepository.findAllById(userIds);
//            if (targetUsers.size() != userIds.size()) {
//                log.warn("Some users not found during announcement. Expected: {}, Found: {}", userIds.size(), targetUsers.size());
//                throw ExceptionUtils.resourceNotFound("User", "id", userIds.toString());
//            }
//            log.debug("Sending announcement to specific users: {}", targetUsers.size());
//        }
//
//        // Log announcement
//        AuditLog announcementLog = new AuditLog();
//        announcementLog.setAction("SYSTEM_ANNOUNCEMENT_SENT");
//        announcementLog.setEntityType("SYSTEM");
//        announcementLog.setEntityId(null);
//        announcementLog.setNewValues("{\"title\": \"" + title + "\", \"message\": \"" + message +
//                "\", \"recipients\": " + targetUsers.size() + "}");
//        announcementLog.setTimestamp(LocalDateTime.now());
//        auditLogRepository.save(announcementLog);
//
//        log.info("System announcement sent successfully to {} users", targetUsers.size());
//    }
//
//    // ===== PRIVATE HELPER METHODS =====
//
//    private String getSystemUptime() {
//        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
//        long days = uptime / (1000 * 60 * 60 * 24);
//        long hours = (uptime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
//        long minutes = (uptime % (1000 * 60 * 60)) / (1000 * 60);
//
//        return String.format("%d days, %d hours, %d minutes", days, hours, minutes);
//    }
//
//    private boolean isUserOnline(Long userId) {
//        LocalDateTime last15Minutes = LocalDateTime.now().minusMinutes(15);
//        return auditLogRepository.findByTimestampBetween(last15Minutes, LocalDateTime.now())
//                .stream()
//                .anyMatch(log -> log.getUser() != null && log.getUser().getUserId().equals(userId));
//    }
//
//    private double calculatePlacementSuccessRate() {
//        long totalApplications = applicationRepository.count();
//        long successfulPlacements = placementRepository.count();
//
//        if (totalApplications == 0) {
//            return 0.0;
//        }
//
//        double successRate = (double) successfulPlacements / totalApplications * 100;
//        log.debug("Placement success rate calculated: {}%", successRate);
//        return successRate;
//    }
//
//    private String determineOverallStatus(String databaseStatus, double memoryUsage, double systemLoad) {
//        if ("HEALTHY".equals(databaseStatus) && memoryUsage < 90 && systemLoad < 80) {
//            return "OPERATIONAL";
//        } else if (memoryUsage >= 90 || systemLoad >= 80) {
//            return "DEGRADED";
//        } else {
//            return "UNHEALTHY";
//        }
//    }
//}

package com.jobportal.serviceImpl;

import com.jobportal.dto.AuditLogDTO;
import com.jobportal.dto.SystemHealthDTO;
import com.jobportal.dto.UserActivityDTO;
import com.jobportal.entity.AuditLog;
import com.jobportal.entity.User;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.repository.*;
import com.jobportal.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final PlacementRepository placementRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    public List<AuditLogDTO> getAuditLogs() {
        log.debug("Fetching audit logs for last 30 days");

        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        return auditLogRepository.findByTimestampBetween(startDate, endDate)
                .stream()
                .map(AuditLogDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getSystemStatistics() {
        log.debug("Generating system statistics");

        Map<String, Object> stats = new HashMap<>();
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // User statistics
        stats.put("totalUsers", userRepository.count());
        stats.put("activeUsers", userRepository.countByIsActive(true));
        stats.put("newUsersThisMonth", userRepository.countUsersCreatedAfter(thirtyDaysAgo));
        stats.put("newConsultantsThisMonth", userRepository.countConsultantsCreatedAfter(thirtyDaysAgo));

        // Placement statistics
        stats.put("totalPlacements", placementRepository.count());
        stats.put("activePlacements", placementRepository.countActivePlacements());
        stats.put("placementSuccessRate", calculatePlacementSuccessRate());

        // Financial statistics
        Double totalRevenue = paymentRepository.sumPaymentsSince(LocalDateTime.now().minusMonths(1).toLocalDate());
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);

        Double outstandingRevenue = invoiceRepository.sumOutstandingAmount();
        stats.put("outstandingRevenue", outstandingRevenue != null ? outstandingRevenue : 0.0);

        // System statistics
        stats.put("auditLogsCount", auditLogRepository.count());
        stats.put("systemUptime", getSystemUptime());

        return stats;
    }

    @Override
    public SystemHealthDTO getSystemHealth() {
        log.debug("Checking system health");

        SystemHealthDTO health = new SystemHealthDTO();

        // Database connectivity check
        try {
            userRepository.count();
            health.setDatabaseStatus("HEALTHY");
            log.debug("Database connectivity: HEALTHY");
        } catch (Exception e) {
            health.setDatabaseStatus("UNHEALTHY - " + e.getMessage());
            log.error("Database connectivity check failed: {}", e.getMessage());
        }

        // User statistics
        health.setTotalUsers(userRepository.count());
        health.setActiveUsers(userRepository.countByIsActive(true));

        // Placement statistics
        health.setActivePlacements(placementRepository.countActivePlacements());

        // Financial statistics
        Double outstandingRevenue = invoiceRepository.sumOutstandingAmount();
        health.setOutstandingRevenue(outstandingRevenue != null ? outstandingRevenue : 0.0);

        // System information
        health.setServerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        health.setUptime(getSystemUptime());

        // Memory usage
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
        double memoryUsage = maxMemory > 0 ? (double) usedMemory / maxMemory * 100 : 0;
        health.setMemoryUsage(Math.round(memoryUsage * 100.0) / 100.0);

        // CPU usage
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double systemLoad = osBean.getSystemLoadAverage();
        health.setCpuUsage(systemLoad >= 0 ? Math.round(systemLoad * 100.0) / 100.0 : 0.0);

        // Overall status determination
        String overallStatus = determineOverallStatus(health.getDatabaseStatus(), memoryUsage, systemLoad);
        health.setStatus(overallStatus);

        log.debug("System health check completed. Status: {}", overallStatus);
        return health;
    }

    @Override
    public List<UserActivityDTO> getUserActivity() {
        log.debug("Fetching user activity for last 24 hours");

        LocalDateTime startTime = LocalDateTime.now().minusHours(24);

        return auditLogRepository.findByTimestampBetween(startTime, LocalDateTime.now())
                .stream()
                .filter(log -> log.getUser() != null) // Only logs with users
                .collect(Collectors.groupingBy(AuditLog::getUser))
                .entrySet()
                .stream()
                .map(entry -> {
                    User user = entry.getKey();
                    List<AuditLog> userLogs = entry.getValue();

                    UserActivityDTO activity = new UserActivityDTO();
                    activity.setUserId(user.getUserId());
                    activity.setUsername(user.getUsername());
                    activity.setUserType(user.getUserType().toString());
                    activity.setLastAction(userLogs.get(0).getAction());
                    activity.setLastActive(userLogs.get(0).getTimestamp());
                    activity.setIpAddress(userLogs.get(0).getIpAddress());
                    activity.setIsOnline(isUserOnline(user.getUserId()));

                    return activity;
                })
                .sorted((a1, a2) -> a2.getLastActive().compareTo(a1.getLastActive()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String createBackup() {
        log.info("Initiating system backup");

        SystemHealthDTO health = getSystemHealth();
        if (!"OPERATIONAL".equals(health.getStatus())) {
            log.warn("Backup aborted due to system status: {}", health.getStatus());
            throw ExceptionUtils.businessError("Cannot create backup when system status is: " + health.getStatus());
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFileName = "backup_" + timestamp + ".sql";

        try {
            log.debug("Simulating backup process for file: {}", backupFileName);
            Thread.sleep(2000); // Simulate backup time

            // Log backup operation
            AuditLog backupLog = new AuditLog();
            backupLog.setAction("SYSTEM_BACKUP_CREATED");
            backupLog.setEntityType("SYSTEM");
            backupLog.setEntityId(null);
            backupLog.setNewValues("{\"backupFile\": \"" + backupFileName + "\"}");
            backupLog.setTimestamp(LocalDateTime.now());
            auditLogRepository.save(backupLog);

            log.info("Backup created successfully: {}", backupFileName);
            return backupFileName;
        } catch (InterruptedException e) {
            log.error("Backup process interrupted", e);
            Thread.currentThread().interrupt();
            throw ExceptionUtils.businessError("Backup process interrupted");
        }
    }

    @Override
    @Transactional
    public void cleanupOldData() {
        log.info("Initiating old data cleanup");

        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(6);

        List<AuditLog> oldLogs = auditLogRepository.findByTimestampBetween(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                cutoffDate
        );

        if (!oldLogs.isEmpty()) {
            log.debug("Cleaning up {} old audit logs", oldLogs.size());
            auditLogRepository.deleteAll(oldLogs);

            // Log cleanup operation
            AuditLog cleanupLog = new AuditLog();
            cleanupLog.setAction("SYSTEM_DATA_CLEANUP");
            cleanupLog.setEntityType("SYSTEM");
            cleanupLog.setEntityId(null);
            cleanupLog.setNewValues("{\"cleanedRecords\": " + oldLogs.size() + ", \"cutoffDate\": \"" + cutoffDate + "\"}");
            cleanupLog.setTimestamp(LocalDateTime.now());
            auditLogRepository.save(cleanupLog);

            log.info("Successfully cleaned up {} old records", oldLogs.size());
        } else {
            log.debug("No old records found for cleanup");
        }
    }

    @Override
    public SystemHealthDTO getSystemMetrics() {
        log.debug("Fetching detailed system metrics");

        SystemHealthDTO health = getSystemHealth();

        // Additional metrics
        health.setTotalPlacements(placementRepository.count());
        health.setPendingInvoices(invoiceRepository.countPendingReview() + invoiceRepository.countAwaitingApproval());

        // System metrics (simplified - in production, use actual monitoring)
        health.setDatabaseConnections(25L);
        health.setActiveSessions(18L);

        return health;
    }

    @Override
    @Transactional
    public void runDatabaseMaintenance() {
        log.info("Starting database maintenance");

        try {
            // Simulate maintenance tasks
            Thread.sleep(5000);

            // Log maintenance operation
            AuditLog maintenanceLog = new AuditLog();
            maintenanceLog.setAction("SYSTEM_DATABASE_MAINTENANCE");
            maintenanceLog.setEntityType("SYSTEM");
            maintenanceLog.setEntityId(null);
            maintenanceLog.setNewValues("{\"maintenanceCompleted\": true, \"timestamp\": \"" + LocalDateTime.now() + "\"}");
            maintenanceLog.setTimestamp(LocalDateTime.now());
            auditLogRepository.save(maintenanceLog);

            log.info("Database maintenance completed successfully");
        } catch (InterruptedException e) {
            log.error("Database maintenance interrupted", e);
            Thread.currentThread().interrupt();
            throw ExceptionUtils.businessError("Database maintenance interrupted");
        }
    }

    @Override
    @Transactional
    public void sendSystemAnnouncement(String title, String message, List<Long> userIds) {
        log.info("Sending system announcement to {} users", userIds != null ? userIds.size() : "all");

        if (title == null || title.trim().isEmpty()) {
            throw ExceptionUtils.validationError("Announcement title is required");
        }
        if (message == null || message.trim().isEmpty()) {
            throw ExceptionUtils.validationError("Announcement message is required");
        }

        List<User> targetUsers;
        if (userIds == null || userIds.isEmpty()) {
            targetUsers = userRepository.findByIsActive(true);
            log.debug("Sending announcement to all active users: {}", targetUsers.size());
        } else {
            targetUsers = userRepository.findAllById(userIds);
            if (targetUsers.size() != userIds.size()) {
                log.warn("Some users not found during announcement. Expected: {}, Found: {}", userIds.size(), targetUsers.size());
                throw ExceptionUtils.resourceNotFound("User", "id", userIds.toString());
            }
            log.debug("Sending announcement to specific users: {}", targetUsers.size());
        }

        // Log announcement
        AuditLog announcementLog = new AuditLog();
        announcementLog.setAction("SYSTEM_ANNOUNCEMENT_SENT");
        announcementLog.setEntityType("SYSTEM");
        announcementLog.setEntityId(null);
        announcementLog.setNewValues("{\"title\": \"" + title + "\", \"message\": \"" + message +
                "\", \"recipients\": " + targetUsers.size() + "}");
        announcementLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(announcementLog);

        log.info("System announcement sent successfully to {} users", targetUsers.size());
    }

    // ===== PRIVATE HELPER METHODS =====

    private String getSystemUptime() {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        long days = uptime / (1000 * 60 * 60 * 24);
        long hours = (uptime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (uptime % (1000 * 60 * 60)) / (1000 * 60);

        return String.format("%d days, %d hours, %d minutes", days, hours, minutes);
    }

    private boolean isUserOnline(Long userId) {
        LocalDateTime last15Minutes = LocalDateTime.now().minusMinutes(15);
        return auditLogRepository.findByTimestampBetween(last15Minutes, LocalDateTime.now())
                .stream()
                .anyMatch(log -> log.getUser() != null && log.getUser().getUserId().equals(userId));
    }

    private double calculatePlacementSuccessRate() {
        long totalApplications = applicationRepository.count();
        long successfulPlacements = placementRepository.count();

        if (totalApplications == 0) {
            return 0.0;
        }

        double successRate = (double) successfulPlacements / totalApplications * 100;
        log.debug("Placement success rate calculated: {}%", successRate);
        return successRate;
    }

    private String determineOverallStatus(String databaseStatus, double memoryUsage, double systemLoad) {
        if ("HEALTHY".equals(databaseStatus) && memoryUsage < 90 && systemLoad < 80) {
            return "OPERATIONAL";
        } else if (memoryUsage >= 90 || systemLoad >= 80) {
            return "DEGRADED";
        } else {
            return "UNHEALTHY";
        }
    }
}