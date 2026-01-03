package com.roomreservation.calendarservice.controller;

import com.roomreservation.calendarservice.dto.CalendarEntryDto;
import com.roomreservation.calendarservice.service.CalendarEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarEntryService calendarEntryService;

    @GetMapping("/day")
    public List<CalendarEntryDto> getDayReservations(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return calendarEntryService.getDayReservations(date);
    }
}
