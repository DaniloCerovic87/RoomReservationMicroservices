package com.roomreservation.reservationservice.model;

import com.roomreservation.reservationservice.model.enums.ReservationStatus;
import com.roomreservation.reservationservice.model.enums.ReservationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "reservation")
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReservationType reservationType;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus reservationStatus;

    @Column(nullable = false, length = 150)
    private String reservationName;

    @CreatedDate
    private LocalDateTime createdAt;
}