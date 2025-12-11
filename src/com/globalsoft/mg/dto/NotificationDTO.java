package com.jobportal.dto;

import com.jobportal.entity.Notification;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long notificationId;
    private UserDTO user;
    private String title;
    private String message;
    private Notification.NotificationType type;
    private Boolean isRead;
    private Long relatedEntityId;
    private LocalDateTime createdAt;
    
    public static NotificationDTO fromEntity(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(notification.getNotificationId());
        dto.setUser(UserDTO.fromEntity(notification.getUser()));
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setIsRead(notification.getIsRead());
        dto.setRelatedEntityId(notification.getRelatedEntityId());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}