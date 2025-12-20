package com.roomreservation.notificationservice.service;

import com.roomreservation.notificationservice.event.UserInvitedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@example.com}")
    private String from;

    @Value("${app.frontend.base-url:http://localhost:4200}")
    private String frontendBaseUrl;

    public void sendActivationEmail(UserInvitedEvent event) {
        String activationLink = frontendBaseUrl + "/complete-registration?token=" + event.activationToken();

        String expiryText = "";
        if (event.activationExpiresAt() != null) {
            expiryText = "\n\nThis link is valid until: " + event.activationExpiresAt()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
        }

        SimpleMailMessage msg = getMailMessage(event, activationLink, expiryText);

        try {
            mailSender.send(msg);
            log.info("Activation email sent to {} (userId={})", event.email(), event.userId());
        } catch (MailException e) {
            log.error("Exception occurred when sending mail", e);
            throw new RuntimeException("Exception occurred when sending mail to " + event.email(), e);
        }
    }

    private SimpleMailMessage getMailMessage(UserInvitedEvent event, String activationLink, String expiryText) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(event.email());
        msg.setSubject("Activate your account");
        msg.setText("""
                Hi %s,
                
                You’ve been invited to create an account.
                Please activate your account by clicking the link below:
                
                %s
                %s
                
                If you didn’t request this, you can safely ignore this email.
                
                Best regards,
                Faculty Room Reservation System
                """.formatted(
                event.firstName(),
                activationLink,
                expiryText
        ));
        return msg;
    }
}
