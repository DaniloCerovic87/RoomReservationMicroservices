package com.roomreservation.calendarservice.service.impl;

import com.roomreservation.calendarservice.event.ReservationCreatedEvent;
import com.roomreservation.calendarservice.model.CalendarEntry;
import com.roomreservation.calendarservice.model.EmployeeSummary;
import com.roomreservation.calendarservice.model.RoomSummary;
import com.roomreservation.calendarservice.repository.CalendarEntryRepository;
import com.roomreservation.calendarservice.service.CalendarEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalendarEntryServiceImpl implements CalendarEntryService {

    private final CalendarEntryRepository repo;

    @Transactional
    public void applyReservationCreated(ReservationCreatedEvent event) {
        for (var room : event.rooms()) {
            String id = buildId(event.reservationId(), room.roomId());

            CalendarEntry entry = new CalendarEntry();
            entry.setId(id);
            entry.setReservationId(event.reservationId());

            entry.setEmployee(EmployeeSummary.builder()
                    .id(event.employee().employeeId())
                    .fullName(event.employee().fullName())
                    .build());

            entry.setRoom(RoomSummary.builder()
                    .id(room.roomId())
                    .name(room.name())
                    .build());

            entry.setReservationName(event.reservationName());
            entry.setReservationType(event.reservationType());
            entry.setStatus(event.status());

            entry.setStartTime(event.startTime());
            entry.setEndTime(event.endTime());

            entry.setOccurredAt(event.occurredAt());

            repo.save(entry);
        }
    }

    private String buildId(long reservationId, long roomId) {
        return "res-%d_room-%d".formatted(reservationId, roomId);
    }
}
