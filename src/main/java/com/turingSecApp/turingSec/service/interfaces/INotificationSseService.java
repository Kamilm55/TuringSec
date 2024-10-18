package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.model.entities.message.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface INotificationSseService {
    public SseEmitter addEmitter();
    public void notifyUserStatusChange(Notification notification);

}
