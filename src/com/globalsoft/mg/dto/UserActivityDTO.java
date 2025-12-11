package com.jobportal.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserActivityDTO {
    private Long userId;
    private String username;
    private String userType;
    private String lastAction;
    private LocalDateTime lastActive;
    private String ipAddress;
    private Boolean isOnline;
}