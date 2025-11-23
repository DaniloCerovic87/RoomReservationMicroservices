package com.roomreservation.reservationservice.model;

import com.roomreservation.reservationservice.model.enums.ClassType;
import com.roomreservation.reservationservice.model.enums.ExamType;
import com.roomreservation.reservationservice.model.enums.ReservationPurpose;
import com.roomreservation.reservationservice.model.enums.ReservationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private Long employeeId;

    @NotNull
    private Long roomId;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ReservationStatus reservationStatus;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ReservationPurpose reservationPurpose;

    private String subject;
    private Integer semester;

    @Enumerated(EnumType.STRING)
    private ClassType classType;

    @Enumerated(EnumType.STRING)
    private ExamType examType;

    private String meetingName;
    private String meetingDescription;

    private String eventName;
    private String eventDescription;

    @CreatedDate
    private LocalDateTime createdAt;
}