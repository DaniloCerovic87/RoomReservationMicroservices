package com.roomreservation.reservationservice.messaging.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.roomreservation.reservationservice.messaging.event.ReservationCreatedEvent;
import com.roomreservation.reservationservice.messaging.event.ReservationStatusChangedEvent;
import com.roomreservation.reservationservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxRepo;
    private final OutboxPayloadMapper outboxPayloadMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final int BATCH_SIZE = 20;
    private static final int MAX_RETRIES = 30;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishBatch() {
        List<OutboxEvent> batch = outboxRepo.lockNextBatch(BATCH_SIZE, MAX_RETRIES);
        if (batch.isEmpty()) {
            return;
        }

        for (OutboxEvent e : batch) {
            try {
                Object evt = switch (e.getEventType()) {
                    case "ReservationCreatedEvent" ->
                            outboxPayloadMapper.fromJson(e.getPayload(), ReservationCreatedEvent.class);
                    case "ReservationStatusChangedEvent" ->
                            outboxPayloadMapper.fromJson(e.getPayload(), ReservationStatusChangedEvent.class);
                    default -> throw new IllegalStateException("Unknown eventType=" + e.getEventType());
                };

                var sendResult = kafkaTemplate
                        .send(e.getTopic(), e.getEventKey(), evt)
                        .get(3, TimeUnit.SECONDS);

                e.setStatus(OutboxStatus.SENT);
                e.setSentAt(LocalDateTime.now());
                e.setLastError(null);

                log.info("Outbox SENT id={} topic={} partition={} offset={}",
                        e.getId(),
                        e.getTopic(),
                        sendResult.getRecordMetadata().partition(),
                        sendResult.getRecordMetadata().offset());

            } catch (Exception ex) {
                e.setLastError(shorten(rootCauseNameAndMessage(ex)));
                e.setStatus(OutboxStatus.FAILED);

                if (isPermanent(ex)) {
                    e.setRetryCount(MAX_RETRIES);
                    log.error("Outbox DEAD id={} err={}", e.getId(), e.getLastError(), ex);
                } else {
                    e.setRetryCount(e.getRetryCount() + 1);
                    if (e.getRetryCount() % 10 == 0) {
                        log.error("Outbox FAILED id={} retries={} err={}", e.getId(), e.getRetryCount(), e.getLastError(), ex);
                    } else {
                        log.warn("Outbox FAILED id={} retries={} err={}", e.getId(), e.getRetryCount(), e.getLastError());
                    }
                }
            }
        }
        outboxRepo.saveAll(batch);
    }

    private static boolean isPermanent(Throwable ex) {
        Throwable t = ex;
        while (t != null) {
            if (t instanceof JsonProcessingException) return true;
            if (t instanceof SerializationException) return true;
            if (t instanceof AuthorizationException) return true;
            if (t instanceof InvalidTopicException) return true;
            if (t instanceof RecordTooLargeException) return true;
            t = t.getCause();
        }
        return false;
    }

    private String rootCauseNameAndMessage(Throwable ex) {
        Throwable t = ex;
        int guard = 0;
        while (t.getCause() != null && t.getCause() != t && guard++ < 20) {
            t = t.getCause();
        }
        String out = t.getClass().getSimpleName() + ": " + t.getMessage();
        log.warn("LAST_ERROR_TO_STORE = [{}]", out);
        return shorten(t.getClass().getSimpleName() + ": " + t.getMessage());
    }

    private static String shorten(String s) {
        int max = 2000;
        if (s == null) {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max);
    }
}
