package com.turingSecApp.turingSec.model.repository;

import com.turingSecApp.turingSec.model.entities.message.Notification;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findNotificationsByUser(UserEntity user);
}
