package com.jobportal.dto;

import com.jobportal.entity.AuditLog;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLogDTO {
    private Long logId;
    private UserDTO user;
    private String action;
    private String entityType;
    private Long entityId;
    private String oldValues;
    private String newValues;
    private String ipAddress;
    private LocalDateTime timestamp;
    
    public static AuditLogDTO fromEntity(AuditLog auditLog) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setLogId(auditLog.getLogId());
        
        if (auditLog.getUser() != null) {
            dto.setUser(UserDTO.fromEntity(auditLog.getUser()));
        }
        
        dto.setAction(auditLog.getAction());
        dto.setEntityType(auditLog.getEntityType());
        dto.setEntityId(auditLog.getEntityId());
        dto.setOldValues(auditLog.getOldValues());
        dto.setNewValues(auditLog.getNewValues());
        dto.setIpAddress(auditLog.getIpAddress());
        dto.setTimestamp(auditLog.getTimestamp());
        return dto;
    }
}