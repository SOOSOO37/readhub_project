package com.readhub.backend.notification.mapper;

import com.readhub.backend.notification.dto.NotificationResponseDto;
import com.readhub.backend.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NotificationMapper {

    NotificationMapper NOTIFICATION_MAPPER = Mappers.getMapper(NotificationMapper.class);
    NotificationResponseDto NotificationToNotificationResponseDto(Notification notification);
}
