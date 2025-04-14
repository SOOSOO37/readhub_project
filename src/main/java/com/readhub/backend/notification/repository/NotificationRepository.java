package com.readhub.backend.notification.repository;

import com.readhub.backend.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
}
