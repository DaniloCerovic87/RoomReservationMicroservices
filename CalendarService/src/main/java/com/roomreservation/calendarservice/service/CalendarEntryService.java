package com.roomreservation.calendarservice.service;

import com.roomreservation.calendarservice.event.ReservationCreatedEvent;

public interface CalendarEntryService {

    void applyReservationCreated(ReservationCreatedEvent event);

}
