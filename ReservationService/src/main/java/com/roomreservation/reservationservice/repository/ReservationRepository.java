package com.roomreservation.reservationservice.repository;

import com.roomreservation.reservationservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
