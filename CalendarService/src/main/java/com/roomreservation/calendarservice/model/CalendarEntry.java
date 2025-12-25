package com.roomreservation.calendarservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("calendar_entries")
public class CalendarEntry {

    @Id
    private String id;

    private Long reservationId;

    private EmployeeSummary employee;
    private RoomSummary room;

    private String reservationName;
    private String reservationType;
    private String status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private LocalDateTime occurredAt;

}
