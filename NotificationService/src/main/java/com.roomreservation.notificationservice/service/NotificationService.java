package com.roomreservation.notificationservice.service;

import com.roomreservation.notificationservice.messaging.event.UserInvitedEvent;

public interface NotificationService {

    void sendActivationEmail(UserInvitedEvent event);

}
