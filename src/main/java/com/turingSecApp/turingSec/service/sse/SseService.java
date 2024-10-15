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
        UserEntity regularUser = (UserEntity) userFactory.getAuthenticatedBaseUser();
        UserEntity user = utilService.findUserById(regularUser.getId());

        if(emitters.get(user.getId()) == null){
            SseEmitter newEmitter = new SseEmitter();
            emitters.put(user.getId(), newEmitter); //Creating emitter for specific user id, and putting it to the map
        }

        SseEmitter emitter = emitters.get(user.getId()); //Finding exact Hacker emitter
        emitter.onCompletion(() -> emitters.remove(user.getId()));
        emitter.onTimeout(() -> emitters.remove(user.getId()));
        return emitter;
    }

    public void notifyUserStatusChange(Notification notification) {
        UserEntity user = notification.getUser();

        SseEmitter emitter = emitters.get(user.getId()); //Finding exact user emitter
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(notification)); //Sending updates
            } catch (IOException e) {
                emitters.remove(user.getId());
            }
            log.info("Sending real time updates to user");
        }
        else {
            try {
                log.info("Emitter was not found with hacker id: "+user.getHacker().getId());
                SseEmitter newEmitter = new SseEmitter();
                emitters.put(user.getId(), newEmitter);
                newEmitter.send(SseEmitter.event().data(notification));

            }catch (IOException e){
                emitters.remove(user.getId());
            }
            log.info("Sending real time updates to user after creating new emitter");
        }

    }
}
