package com.roomreservation.employeeservice.security;

public record JwtUserPrincipal(
        Long employeeId,
        String role
) {
}
