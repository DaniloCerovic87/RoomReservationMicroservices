package com.roomreservation.reservationservice.repository;

import com.roomreservation.reservationservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = """
                SELECT EXISTS (
                    SELECT 1
                    FROM reservation r
                    JOIN reservation_room rr ON rr.reservation_id = r.id
                    WHERE rr.room_id IN (:roomIds)
                      AND r.reservation_status IN ('PENDING','APPROVED')
                      AND r.start_time < :endTime
                      AND r.end_time > :startTime
                )
            """, nativeQuery = true)
    boolean existsOverlappingReservation(
            @Param("roomIds") List<Long> roomIds,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
