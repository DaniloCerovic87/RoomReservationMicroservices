package com.roomreservation.notificationservice.service;

import com.roomreservation.notificationservice.messaging.event.InviteEmailFailedFinalEvent;
import com.roomreservation.notificationservice.messaging.event.UserInvitedEvent;
import com.roomreservation.notificationservice.exception.ActivationEmailSendingException;
import com.roomreservation.notificationservice.model.InviteEmailDelivery;
import com.roomreservation.notificationservice.model.InviteEmailDeliveryStatus;
import com.roomreservation.notificationservice.repository.InviteEmailDeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private static final int MAX_RETRY_COUNT = 3;

    private final JavaMailSender mailSender;
    private final InviteEmailDeliveryRepository inviteEmailDeliveryRepository;
    private final KafkaTemplate<String, InviteEmailFailedFinalEvent> kafkaTemplate;

    @Value("${spring.mail.username:no-reply@example.com}")
    private String from;

    @Value("${app.frontend.base-url:http://localhost:4200}")
    private String frontendBaseUrl;

    public void sendActivationEmail(UserInvitedEvent event) {
        InviteEmailDelivery delivery = inviteEmailDeliveryRepository.findById(event.userId())
                .orElseGet(() -> createProcessingRecord(event));

        if (delivery.getStatus() == InviteEmailDeliveryStatus.SENT) {
            log.info("Invite email already sent for userId={}, skipping duplicate event", event.userId());
            return;
        }

        if (delivery.getStatus() == InviteEmailDeliveryStatus.FAILED_FINAL) {
            log.info("Invite email already marked as FAILED_FINAL for userId={}, skipping duplicate event", event.userId());
            return;
        }

        String activationLink = frontendBaseUrl + "/complete-registration?token=" + event.activationToken();

        String expiryText = "";
        if (event.expiresAt() != null) {
            expiryText = "\n\nThis link is valid until: " +
                    event.expiresAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
        }

        SimpleMailMessage msg = getMailMessage(event, activationLink, expiryText);

        try {
            mailSender.send(msg);

            delivery.setStatus(InviteEmailDeliveryStatus.SENT);
            delivery.setLastError(null);
            delivery.setUpdatedAt(LocalDateTime.now());
            inviteEmailDeliveryRepository.save(delivery);

            log.info("Activation email sent successfully to {} (userId={})", event.email(), event.userId());

        } catch (MailException ex) {
            handleMailFailure(delivery, event, ex);
        }
    }

    private InviteEmailDelivery createProcessingRecord(UserInvitedEvent event) {
        InviteEmailDelivery delivery = InviteEmailDelivery.builder()
                .userId(event.userId())
                .employeeId(event.employeeId())
                .status(InviteEmailDeliveryStatus.PROCESSING)
                .retryCount(0)
                .lastError(null)
                .updatedAt(LocalDateTime.now())
                .build();

        return inviteEmailDeliveryRepository.save(delivery);
    }

    private void handleMailFailure(InviteEmailDelivery delivery,
                                   UserInvitedEvent event,
                                   MailException ex) {

        int nextRetryCount = delivery.getRetryCount() + 1;
        delivery.setRetryCount(nextRetryCount);
        delivery.setLastError(ex.getMessage());
        delivery.setUpdatedAt(LocalDateTime.now());

        if (nextRetryCount >= MAX_RETRY_COUNT) {
            delivery.setStatus(InviteEmailDeliveryStatus.FAILED_FINAL);
            inviteEmailDeliveryRepository.save(delivery);

            log.error("Activation email permanently failed for userId={}, employeeId={}",
                    event.userId(), event.employeeId(), ex);

            InviteEmailFailedFinalEvent failedEvent = new InviteEmailFailedFinalEvent(
                    event.userId(),
                    event.employeeId(),
                    ex.getMessage(),
                    LocalDateTime.now()
            );

            kafkaTemplate.send("invite-email.failed", failedEvent);
            return;
        }

        inviteEmailDeliveryRepository.save(delivery);

        log.warn("Activation email failed for userId={}, retryCount={}/{}",
                event.userId(), nextRetryCount, MAX_RETRY_COUNT, ex);

        throw new ActivationEmailSendingException("Mail sending failed for userId=" + event.userId());
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
