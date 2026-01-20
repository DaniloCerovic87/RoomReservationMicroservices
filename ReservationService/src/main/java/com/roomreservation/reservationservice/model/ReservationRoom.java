package com.roomreservation.reservationservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reservation_room",
        uniqueConstraints = @UniqueConstraint(name = "uk_reservation_room", columnNames = {"reservation_id", "room_id"}))
@Getter
@Setter
public class ReservationRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    private Long roomId;

}