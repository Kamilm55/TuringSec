package com.turingSecApp.turingSec.service.sse;

import com.turingSecApp.turingSec.model.entities.message.Notification;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.NotificationRepository;
import com.turingSecApp.turingSec.service.interfaces.ISseService;
import com.turingSecApp.turingSec.service.user.factory.UserFactory;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RequiredArgsConstructor
@Service
@Slf4j
public class SseService implements ISseService {
    private final UserFactory userFactory;
    private final UtilService utilService;

    private Map<String, SseEmitter> emitters = new HashMap<>();


    public SseEmitter addEmitter()
    {
        log.info("Creating emitter for user");
        UserEntity regularUser = (UserEntity) userFactory.getAuthenticatedBaseUser();
        UserEntity user = utilService.findUserById(regularUser.getId());

        SseEmitter emitter = new SseEmitter();
        emitters.put(user.getId(), emitter); //Creating emitter for specific user id, and putting it to the map
        emitter.onCompletion(() -> emitters.remove(user.getId()));
        emitter.onTimeout(() -> emitters.remove(user.getId()));
        return emitter;
    }

    public void notifyUserStatusChange(Notification notification) {
        UserEntity regularUser = (UserEntity) userFactory.getAuthenticatedBaseUser();
        UserEntity user = utilService.findUserById(regularUser.getId());

        SseEmitter emitter = emitters.get(user.getId()); //Finding exact user emitter
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(notification)); //Sending updates
            } catch (IOException e) {
                emitters.remove(user.getId());
            }
        }
        log.info("Sending real time updates to user");
    }
}
