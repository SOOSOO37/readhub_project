package com.readhub.backend.notification.repository;

import com.readhub.backend.notification.entity.Notification;
import com.readhub.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    boolean existsByReceiverAndNotificationTypeAndContent(User receiver, Notification.NotificationType notificationType, String content);
}
