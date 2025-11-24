package com.roomreservation.employeeservice.dto;

public record InviteUserRequest(
        String email,
        String username,
        Long employeeId
) {
}
