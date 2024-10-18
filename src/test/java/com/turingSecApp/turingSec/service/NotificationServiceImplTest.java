package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.model.entities.message.Notification;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.NotificationRepository;
import com.turingSecApp.turingSec.response.message.NotificationDto;
import com.turingSecApp.turingSec.service.interfaces.INotificationSseService;
import com.turingSecApp.turingSec.service.user.factory.UserFactory;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.NotificationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class NotificationServiceImplTest {

        @Mock
        private NotificationRepository notificationRepository;

        @Mock
        private UserFactory userFactory;

        @Mock
        private UtilService utilService;

        @Mock
        private INotificationSseService sseService;

        @Mock
        private NotificationMapper notificationMapper;

        @InjectMocks
        private NotificationService notificationService;

        private UserEntity user;
        private Notification notification;

        @BeforeEach
        void setup(){
        MockitoAnnotations.openMocks(this);

        user = new UserEntity();
        user.setId(UUID.randomUUID().toString());

        notification = Notification.builder()
                .id(1L)
                .message("Test message")
                .type("INFO")
                .sendTime(LocalDateTime.now())
                .user(user)
                .build();
    }

        @Test
        void testSaveNotification() {
        notificationService.saveNotification(user, "Test Notification", "info");

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(sseService, times(1)).notifyUserStatusChange(any(Notification.class));
    }

        @Test
        void testGetAllNotificationsByUser() {
        when(userFactory.getAuthenticatedBaseUser()).thenReturn(user);
        when(utilService.findUserById(any())).thenReturn(user);
        when(notificationRepository.findNotificationsByUser(user)).thenReturn(Collections.singletonList(notification));

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setId(1L);
        notificationDto.setMessage("Test message");
        when(notificationMapper.notificationToNotificationDto(notification)).thenReturn(notificationDto);

        List<NotificationDto> notifications = notificationService.getAllNotificationsByUser();

        assertNotNull(notifications);
        assertEquals(1, notifications.size());
        assertEquals("Test message", notifications.get(0).getMessage());

        verify(notificationRepository, times(1)).findNotificationsByUser(user);
    }
}
