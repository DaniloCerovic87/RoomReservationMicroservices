package com.roomreservation.reservationservice.messaging.outbox;

import com.roomreservation.reservationservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OutboxEventEnqueuer {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxPayloadMapper outboxPayloadMapper;

    public void enqueueReservationEvent(Long reservationId,
                                        String eventType,
                                        String topic,
                                        Object payload) {

        LocalDateTime now = LocalDateTime.now();

        OutboxEvent out = new OutboxEvent();
        out.setAggregateType("Reservation");
        out.setAggregateId(reservationId);
        out.setEventType(eventType);
        out.setTopic(topic);
        out.setEventKey(String.valueOf(reservationId));

        out.setStatus(OutboxStatus.NEW);
        out.setRetryCount(0);
        out.setLastError(null);
        out.setCreatedAt(now);
        out.setSentAt(null);

        out.setPayload(outboxPayloadMapper.toJson(payload));

        outboxEventRepository.save(out);
    }
}
