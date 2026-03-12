package com.roomreservation.calendarservice.messaging.listener;

import com.roomreservation.calendarservice.messaging.event.ReservationCreatedEvent;
import com.roomreservation.calendarservice.messaging.event.ReservationRoomStatusChangedEvent;
import com.roomreservation.calendarservice.service.CalendarEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventsListener {

    private final CalendarEntryService calendarEntryService;

    @KafkaListener(topics = "reservation-created", groupId = "calendar-service")
    public void onReservationCreated(ReservationCreatedEvent event) {
        calendarEntryService.applyReservationCreated(event);
    }

    @KafkaListener(topics = "reservation-room-status-changed", groupId = "calendar-service")
    public void onReservationRoomStatusChanged(ReservationRoomStatusChangedEvent event) {
        calendarEntryService.applyReservationRoomStatusChanged(event);
    }

}
