package com.jobportal.repository;

import com.jobportal.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserUserId(Long userId);

    List<Notification> findByUserUserIdAndIsRead(Long userId, Boolean isRead);

    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.userId = :userId AND n.isRead = false")
    Long countUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE n.isRead = false")
    List<Notification> findAllUnread();

    @Query("DELETE FROM Notification n WHERE n.user.userId = :userId AND n.isRead = true")
    void deleteReadByUserId(@Param("userId") Long userId);
}