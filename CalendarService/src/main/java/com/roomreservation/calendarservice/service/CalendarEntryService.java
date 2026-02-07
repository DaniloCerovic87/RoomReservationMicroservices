package com.roomreservation.calendarservice.service;

import com.roomreservation.calendarservice.dto.CalendarEntryDto;
import com.roomreservation.calendarservice.event.ReservationCreatedEvent;
import com.roomreservation.calendarservice.event.ReservationRoomStatusChangedEvent;

import java.time.LocalDate;
import java.util.List;

public interface CalendarEntryService {

    void applyReservationCreated(ReservationCreatedEvent event);

    List<CalendarEntryDto> getDayReservations(LocalDate date);

    void applyReservationRoomStatusChanged(ReservationRoomStatusChangedEvent event);
}

