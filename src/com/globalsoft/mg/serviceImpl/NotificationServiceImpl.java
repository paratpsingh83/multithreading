package com.jobportal.serviceImpl;

import com.jobportal.dto.NotificationDTO;
import com.jobportal.entity.Notification;
import com.jobportal.entity.User;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.repository.NotificationRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public List<NotificationDTO> getAll() {
        return notificationRepository.findAll()
                .stream()
                .map(NotificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationDTO getById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Notification", "id", id));
        return NotificationDTO.fromEntity(notification);
    }

    @Override
    public List<NotificationDTO> getUnread() {
        return notificationRepository.findAllUnread()
                .stream()
                .map(NotificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationDTO markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Notification", "id", id));

        if (notification.getIsRead()) {
            throw ExceptionUtils.businessError("Notification is already marked as read");
        }

        notification.setIsRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return NotificationDTO.fromEntity(updatedNotification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        List<Notification> unreadNotifications = notificationRepository.findAllUnread();
        if (unreadNotifications.isEmpty()) {
            throw ExceptionUtils.businessError("No unread notifications found");
        }

        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Notification", "id", id));
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void createNotification(Long userId, String title, String message, String type, Long relatedEntityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("User", "id", userId));

        // Validate notification type
        Notification.NotificationType notificationType;
        try {
            notificationType = Notification.NotificationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ExceptionUtils.validationError("Invalid notification type: " + type);
        }

        // Validate title and message
        if (title == null || title.trim().isEmpty()) {
            throw ExceptionUtils.validationError("Notification title is required");
        }
        if (message == null || message.trim().isEmpty()) {
            throw ExceptionUtils.validationError("Notification message is required");
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title.trim());
        notification.setMessage(message.trim());
        notification.setType(notificationType);
        notification.setIsRead(false);
        notification.setRelatedEntityId(relatedEntityId);

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationDTO> getByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw ExceptionUtils.resourceNotFound("User", "id", userId);
        }

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

//    @Override
//    @Transactional
//    public void deleteOldNotifications(int daysOld) {
//        if (daysOld < 1) {
//            throw ExceptionUtils.validationError("Days must be at least 1");
//        }
//
//        // In real implementation, this would delete notifications older than specified days
//        // For now, we'll implement a placeholder
//        java.time.LocalDateTime cutoffDate = java.time.LocalDateTime.now().minusDays(daysOld);
//
//        // This would typically use a query like:
//        // notificationRepository.deleteByCreatedAtBefore(cutoffDate);
//
//        System.out.println("Would delete notifications older than: " + cutoffDate);
//    }

//    @Override
//    @Transactional
//    public void cleanupReadNotifications() {
//        // Delete read notifications older than 30 days
//        java.time.LocalDateTime cutoffDate = java.time.LocalDateTime.now().minusDays(30);
//
//        // In real implementation:
//        // notificationRepository.deleteByIsReadTrueAndCreatedAtBefore(cutoffDate);
//
//        System.out.println("Would cleanup read notifications older than: " + cutoffDate);
//    }
//
//    // Batch notification creation for multiple users
//    @Override
//    @Transactional
//    public void createBatchNotification(List<Long> userIds, String title, String message, String type) {
//        if (userIds == null || userIds.isEmpty()) {
//            throw ExceptionUtils.validationError("User IDs list cannot be empty");
//        }
//
//        // Validate all users exist
//        List<User> users = userRepository.findAllById(userIds);
//        if (users.size() != userIds.size()) {
//            throw ExceptionUtils.resourceNotFound("One or more users not found");
//        }
//
//        Notification.NotificationType notificationType;
//        try {
//            notificationType = Notification.NotificationType.valueOf(type.toUpperCase());
//        } catch (IllegalArgumentException e) {
//            throw ExceptionUtils.validationError("Invalid notification type: " + type);
//        }
//
//        List<Notification> notifications = users.stream()
//                .map(user -> {
//                    Notification notification = new Notification();
//                    notification.setUser(user);
//                    notification.setTitle(title);
//                    notification.setMessage(message);
//                    notification.setType(notificationType);
//                    notification.setIsRead(false);
//                    return notification;
//                })
//                .collect(Collectors.toList());
//
//        notificationRepository.saveAll(notifications);
//    }
}