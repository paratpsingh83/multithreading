//package com.jobportal.dto;
//
//import lombok.Data;
//
//@Data
//public class SystemHealthDTO {
//    private String status;
//    private String databaseStatus;
//    private Long totalUsers;
//    private Long activeUsers;
//    private String serverTime;
//    private String uptime;
//    private Double memoryUsage;
//    private Double cpuUsage;
//}
package com.jobportal.dto;

import lombok.Data;

@Data
public class SystemHealthDTO {
    private String status;
    private String databaseStatus;

    // User statistics
    private Long totalUsers;
    private Long activeUsers;

    // Placement statistics
    private Long activePlacements;
    private Long totalPlacements;

    // Financial statistics
    private Double outstandingRevenue;
    private Long pendingInvoices;

    // System information
    private String serverTime;
    private String uptime;
    private Double memoryUsage;
    private Double cpuUsage;

    // Database/System metrics
    private Long databaseConnections;
    private Long activeSessions;
}
