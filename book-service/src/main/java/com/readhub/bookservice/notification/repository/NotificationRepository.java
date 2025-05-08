package com.readhub.bookservice.notification.repository;

import com.readhub.bookservice.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    boolean existsByReceiverIdAndNotificationTypeAndContent(Long receiverId, Notification.NotificationType notificationType, String content);

}

