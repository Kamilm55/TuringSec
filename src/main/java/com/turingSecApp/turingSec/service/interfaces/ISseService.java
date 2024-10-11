package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.model.entities.message.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

public interface ISseService {
    public SseEmitter addEmitter();
    public void notifyUserStatusChange(Notification notification);

}
