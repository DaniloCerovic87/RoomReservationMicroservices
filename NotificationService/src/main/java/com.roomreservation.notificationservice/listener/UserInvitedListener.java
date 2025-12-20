package com.roomreservation.notificationservice.listener;

import com.roomreservation.notificationservice.event.UserInvitedEvent;
import com.roomreservation.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserInvitedListener {

    private final EmailService mailService;

    @KafkaListener(topics = "${app.kafka.topics.userInvited:user-invited}",
            groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void onUserInvited(UserInvitedEvent event) {
        log.info("Received user.invited event: userId={}, email={}", event.userId(), event.email());
        mailService.sendActivationEmail(event);
    }
}