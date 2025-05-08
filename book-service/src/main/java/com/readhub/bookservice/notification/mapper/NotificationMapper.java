package com.readhub.bookservice.notification.mapper;

import com.readhub.bookservice.notification.dto.NotificationResponseDto;
import com.readhub.bookservice.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponseDto NotificationToNotificationResponseDto(Notification notification);
}
