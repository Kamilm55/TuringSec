package com.turingSecApp.turingSec.service.interfaces;

public interface IEmailNotificationService {
    void sendEmail(String to, String subject, String content);
}
