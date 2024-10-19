package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.model.entities.message.Notification;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.NotificationRepository;
import com.turingSecApp.turingSec.response.message.NotificationDto;
import com.turingSecApp.turingSec.service.interfaces.INotificationService;
import com.turingSecApp.turingSec.service.interfaces.INotificationSseService;
import com.turingSecApp.turingSec.service.user.factory.UserFactory;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final UserFactory userFactory;
    private final UtilService utilService;
    private final INotificationSseService sseService;

    @Override
    public void saveNotification(UserEntity user, String message, String type) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .sendTime(LocalDateTime.now())
                .build();
        sseService.notifyUserStatusChange(notification);

        notificationRepository.save(notification);
        log.info("New notification have been recorded with user id : "+ user.getId());
    }

    @Override
    public List<NotificationDto> getAllNotificationsByUser() {
        UserEntity regularUser = (UserEntity) userFactory.getAuthenticatedBaseUser();
        UserEntity user = utilService.findUserById(regularUser.getId());

         List<Notification> notifications = notificationRepository.findNotificationsByUser(user);
         List<NotificationDto> notificationDtos =notifications.stream().map(NotificationMapper.INSTANCE::notificationToNotificationDto).collect(Collectors.toList());
        log.info("Retrieving all notifications related to user with id : " +user.getId());
        return notificationDtos;
    }
}
