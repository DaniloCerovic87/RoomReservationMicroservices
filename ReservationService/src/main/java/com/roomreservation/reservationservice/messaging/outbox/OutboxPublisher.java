package com.roomreservation.reservationservice.messaging.outbox;

import com.roomreservation.reservationservice.messaging.event.ReservationCreatedEvent;
import com.roomreservation.reservationservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxRepo;
    private final OutboxPayloadMapper outboxPayloadMapper;
    private final KafkaTemplate<String, ReservationCreatedEvent> kafkaTemplate;

    private static final int BATCH_SIZE = 20;
    private static final int MAX_RETRIES = 10;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishBatch() {
        List<OutboxEvent> batch = outboxRepo.lockNextBatch(BATCH_SIZE, MAX_RETRIES);
        if (batch.isEmpty()) {
            return;
        }

        for (OutboxEvent e : batch) {
            try {
                ReservationCreatedEvent evt = outboxPayloadMapper.fromJson(e.getPayload(), ReservationCreatedEvent.class);

                var sendResult = kafkaTemplate
                        .send(e.getTopic(), e.getEventKey(), evt)
                        .get();

                e.setStatus(OutboxStatus.SENT);
                e.setSentAt(LocalDateTime.now());
                e.setLastError(null);

                log.info("Outbox SENT id={} topic={} partition={} offset={}",
                        e.getId(),
                        e.getTopic(),
                        sendResult.getRecordMetadata().partition(),
                        sendResult.getRecordMetadata().offset());

            } catch (Exception ex) {
                e.setRetryCount(e.getRetryCount() + 1);
                e.setStatus(OutboxStatus.FAILED);
                e.setLastError(shorten(ex.getMessage()));

                log.error("Outbox FAILED id={} retries={} err={}",
                        e.getId(), e.getRetryCount(), e.getLastError(), ex);
            }
        }
        outboxRepo.saveAll(batch);
    }

    private static String shorten(String s) {
        int max = 2000;
        if (s == null) {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max);
    }
}
