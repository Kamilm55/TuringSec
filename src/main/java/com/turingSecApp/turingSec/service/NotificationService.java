package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.model.entities.message.Notification;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.NotificationRepository;
import com.turingSecApp.turingSec.response.message.NotificationDto;
import com.turingSecApp.turingSec.service.interfaces.INotificationService;
import com.turingSecApp.turingSec.service.user.factory.UserFactory;
import com.turingSecApp.turingSec.util.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final UserFactory userFactory;

    @Override
    public void sendNotification(UserEntity user, String message, String type) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .sendTime(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationDto> getAllNotificationsByUser() {
        UserEntity user = (UserEntity) userFactory.getAuthenticatedBaseUser();

         List<Notification> notifications = notificationRepository.findNotificationsByUser(user);
            List<NotificationDto> notificationDtos =notifications.stream().map(NotificationMapper.INSTANCE::notificationToNotificationDto).collect(Collectors.toList());
         return notificationDtos;
    }
}
