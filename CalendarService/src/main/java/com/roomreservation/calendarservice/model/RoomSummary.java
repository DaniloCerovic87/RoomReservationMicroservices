package com.roomreservation.calendarservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomSummary {

    private Long id;
    private String name;

}