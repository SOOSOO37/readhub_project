package com.readhub.backend.notification.dto;

import com.readhub.backend.global.utils.LocalDateTimeConverter;
import com.readhub.backend.notification.entity.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationResponseDto {

    private Long notificationId;
    private String content;
    private String url;
    private Boolean isRead;
    private String createdAt;

    @Builder
    public NotificationResponseDto(Notification notification) {
        this.notificationId = notification.getNotificationId();
        this.content = notification.getContent();
        this.url = notification.getUrl();
        this.isRead = notification.getIsRead();
        this.createdAt = LocalDateTimeConverter.timeToString(notification.getCreatedAt());
    }
}
