package com.roomreservation.calendarservice.service.impl;

import com.roomreservation.calendarservice.dto.CalendarEntryDto;
import com.roomreservation.calendarservice.event.ReservationCreatedEvent;
import com.roomreservation.calendarservice.model.CalendarEntry;
import com.roomreservation.calendarservice.model.EmployeeSummary;
import com.roomreservation.calendarservice.model.RoomSummary;
import com.roomreservation.calendarservice.repository.CalendarEntryRepository;
import com.roomreservation.calendarservice.service.CalendarEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarEntryServiceImpl implements CalendarEntryService {

    private final CalendarEntryRepository repo;

    @Override
    @Transactional
    public void applyReservationCreated(ReservationCreatedEvent event) {
        for (var room : event.rooms()) {
            String entryKey = buildEntryKey(event.reservationId(), room.roomId());

            CalendarEntry entry = new CalendarEntry();
            entry.setEntryKey(entryKey);
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

    @Override
    @Transactional(readOnly = true)
    public List<CalendarEntryDto> getDayReservations(LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

        return repo.findByStartTimeLessThanAndEndTimeGreaterThan(dayEnd, dayStart)
                .stream()
                .map(e -> new CalendarEntryDto(
                        e.getRoom().getId(),
                        e.getRoom().getName(),
                        e.getReservationId(),
                        e.getEmployee().getId(),
                        e.getEmployee().getFullName(),
                        e.getReservationName(),
                        e.getReservationType(),
                        e.getStatus(),
                        e.getStartTime(),
                        e.getEndTime()
                ))
                .toList();
    }

    private String buildEntryKey(long reservationId, long roomId) {
        return "res-%d_room-%d".formatted(reservationId, roomId);
    }
}
