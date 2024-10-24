package com.turingSecApp.turingSec.service.sse;
import com.turingSecApp.turingSec.model.entities.message.Notification;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.service.interfaces.INotificationSseService;
import com.turingSecApp.turingSec.service.user.factory.UserFactory;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationSseService implements INotificationSseService {
    private final UserFactory userFactory;
    private final UtilService utilService;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Queue<Notification>> pendingNotifications = new ConcurrentHashMap<>();

    public SseEmitter addEmitter()
    {
        // Finding logged user
        UserEntity regularUser = (UserEntity) userFactory.getAuthenticatedBaseUser();
        UserEntity user = utilService.findUserById(regularUser.getId());

        // Check if there is an emitter with given id, if not create one
        return emitters.computeIfAbsent(user.getId(),id->createNewEmitter(user.getId()));
    }

    public void notifyUserStatusChange(Notification notification) {
        String userId = notification.getUser().getId();
        SseEmitter emitter = emitters.get(userId); //Finding exact user emitter

        if(emitter != null){
            sendNotification(emitter, notification, userId);
        }
        else{
            log.info("User {} is not online. Queuing notification {}",userId,notification.getId());
            queuePendingNotification(userId,notification);
        }

    }

    private SseEmitter createNewEmitter(String userId){
        // Creating emitter for given user id
        SseEmitter emitter = new SseEmitter(30_000L);
        emitter.onCompletion(()->emitters.remove(userId));
        emitter.onTimeout(()->emitters.remove(userId));
        emitter.onError((e)->emitters.remove(userId));
        sendPendingNotifications(userId,emitter);
        return emitter;

    }

    private void sendNotification(SseEmitter emitter, Notification notification,String userId){
        try{
            emitter.send(SseEmitter.event().data(notification)); // it sends notifications to user
            log.info("Sent notification {} to user : {}",notification.getId(),userId);
        }catch(IOException e){
            log.error("Failed to send notification to user : "+userId, e);
            emitters.remove(userId);
            queuePendingNotification(userId,notification); // in case to store notification in memory (Runtime)
        }
    }

    private void queuePendingNotification(String userId, Notification notification){
        // adding a notification to queue for later (will be sent when user logs in)
        pendingNotifications.computeIfAbsent(userId, id->new ConcurrentLinkedDeque<>()).add(notification);
    }

    private void sendPendingNotifications(String userId, SseEmitter emitter){
        // Retrieving notifications from queue
        Queue<Notification> notifications = pendingNotifications.get(userId);
        if(notifications!=null){
            while (!notifications.isEmpty()){
                Notification notification = notifications.poll();
                try{
                    emitter.send(SseEmitter.event().data(notification));
                    log.info("Sent pending notification to user: "+ userId);
                }catch (IOException e ){
                    log.error("Sending pending notification failed for user : "+userId);
                    queuePendingNotification(userId,notification); // in case to store notification in memory (Runtime)
                    break;
                }
            }
        }
        if(notifications==null){
            try{
                emitter.send(SseEmitter.event().data("There is no new notification"));
            }catch (IOException e ){
                emitters.remove(userId);
            }

        }
    }
}
