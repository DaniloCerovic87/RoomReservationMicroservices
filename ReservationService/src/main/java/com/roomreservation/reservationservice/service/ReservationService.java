package com.roomreservation.reservationservice.service;

import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.dto.ReservationResponse;

public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest request);

    boolean hasActiveReservationForRoom(Long roomId);

    void approveReservation(Long id);

    void declineReservation(Long id);
}