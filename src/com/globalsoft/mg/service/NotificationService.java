package com.jobportal.service;

import com.jobportal.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getAll();

    NotificationDTO getById(Long id);

    List<NotificationDTO> getUnread();

    NotificationDTO markAsRead(Long id);

    void markAllAsRead();

    void delete(Long id);

    void createNotification(Long userId, String title, String message, String type, Long relatedEntityId);

    List<NotificationDTO> getByUserId(Long userId);
}