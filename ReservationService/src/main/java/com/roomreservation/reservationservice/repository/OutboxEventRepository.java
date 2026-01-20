package com.roomreservation.reservationservice.repository;

import com.roomreservation.reservationservice.messaging.outbox.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Query(value = """
            SELECT *
            FROM outbox_event
            WHERE status IN ('NEW','FAILED')
              AND retry_count < :maxRetries
            ORDER BY created_at
            FOR UPDATE SKIP LOCKED
            LIMIT :limit
            """, nativeQuery = true)
    List<OutboxEvent> lockNextBatch(@Param("limit") int limit,
                                    @Param("maxRetries") int maxRetries);
}
