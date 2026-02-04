package com.roomreservation.authenticationservice.messaging.outbox;

import com.roomreservation.authenticationservice.messaging.event.UserInvitedEvent;
import com.roomreservation.authenticationservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OutboxEventEnqueuer {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxPayloadMapper outboxPayloadMapper;

    public void enqueueUserInvitedEvent(Long userId,
                                        UserInvitedEvent event) {

        OutboxEvent out = new OutboxEvent();
        out.setAggregateType("User");
        out.setAggregateId(userId);

        out.setEventType("USER_INVITED");
        out.setTopic("user-invited");
        out.setEventKey(String.valueOf(userId));

        out.setStatus(OutboxStatus.NEW);
        out.setRetryCount(0);
        out.setLastError(null);

        out.setCreatedAt(LocalDateTime.now());
        out.setSentAt(null);

        out.setPayload(outboxPayloadMapper.toJson(event));

        outboxEventRepository.save(out);
    }
}
