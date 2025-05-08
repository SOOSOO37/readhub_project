package com.readhub.bookservice.notification.dto;

import com.readhub.global.utils.LocalDateTimeConverter;
import com.readhub.bookservice.notification.entity.Notification;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {

    private Long notificationId;
    private String content;
    private String url;
    private Boolean isRead;
    private String createdAt;

}
