package com.turingSecApp.turingSec.util.mapper;

import com.turingSecApp.turingSec.model.entities.message.Notification;
import com.turingSecApp.turingSec.response.message.NotificationDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    NotificationDto notificationToNotificationDto(Notification notification);
}
