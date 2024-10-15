package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.response.message.NotificationDto;
import com.turingSecApp.turingSec.service.interfaces.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    @GetMapping
    public BaseResponse<List<NotificationDto>> getNotifications() {
        return BaseResponse.success(notificationService.getAllNotificationsByUser());
    }
}
