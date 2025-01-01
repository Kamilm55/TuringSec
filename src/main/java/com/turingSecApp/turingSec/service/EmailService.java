package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.model.enums.EmailTemplate;
import com.turingSecApp.turingSec.util.EmailTemplateProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, EmailTemplate template, Map<String, String> placeholders) {
        try {
            String subject = template.getSubject();
            String body = template.getBody();

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                body = body.replace(placeholder, entry.getValue());
                subject = subject.replace(placeholder, entry.getValue());
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        }
        catch (Exception e) {
            logger.error("Exception occurs while sending email: Description: {}", e.getMessage(), e);
        }
    }

}