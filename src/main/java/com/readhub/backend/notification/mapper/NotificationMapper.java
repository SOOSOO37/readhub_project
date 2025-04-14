package com.readhub.backend.notification.mapper;

import com.readhub.backend.notification.dto.NotificationResponseDto;
import com.readhub.backend.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponseDto NotificationToNotificationResponseDto(Notification notification);
}
