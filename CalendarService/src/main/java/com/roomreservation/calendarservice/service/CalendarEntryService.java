package com.roomreservation.calendarservice.service;

import com.roomreservation.calendarservice.dto.ReservedRoomDto;
import com.roomreservation.calendarservice.event.ReservationCreatedEvent;

import java.time.LocalDate;
import java.util.List;

public interface CalendarEntryService {

    void applyReservationCreated(ReservationCreatedEvent event);

    List<ReservedRoomDto> getReservedRoomsForDay(LocalDate date);
}

