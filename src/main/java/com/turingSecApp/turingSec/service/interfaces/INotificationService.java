package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.response.message.NotificationDto;

import java.util.List;

public interface INotificationService {
    public void saveNotification(UserEntity user, String message, String type);
    public List<NotificationDto> getAllNotificationsByUser();
}
