package com.roomreservation.reservationservice.repository;

import com.roomreservation.reservationservice.model.ReservationRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRoomRepository extends JpaRepository<ReservationRoom, Long> {

    @Query("""
              select distinct rr.roomId
              from ReservationRoom rr
              where rr.reservation.startTime < :endTime
                and rr.reservation.endTime > :startTime
                and rr.reservationStatus in (
                  com.roomreservation.reservationservice.model.enums.ReservationStatus.PENDING,
                  com.roomreservation.reservationservice.model.enums.ReservationStatus.APPROVED
                )
            """)
    List<Long> findBusyRoomIds(LocalDateTime startTime, LocalDateTime endTime);

}
