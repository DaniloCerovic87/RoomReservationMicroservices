package com.roomreservation.calendarservice.listener;

import com.roomreservation.calendarservice.event.ReservationCreatedEvent;
import com.roomreservation.calendarservice.service.CalendarEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationCreatedListener {

    private final CalendarEntryService calendarEntryService;

    @KafkaListener(topics = "reservation-created", groupId = "calendar-service")
    public void onReservationCreated(ReservationCreatedEvent event) {
        calendarEntryService.applyReservationCreated(event);
    }
}
