package com.roomreservation.reservationservice.repository;

import com.roomreservation.reservationservice.model.ReservationRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRoomRepository extends JpaRepository<ReservationRoom, Long> {
}
