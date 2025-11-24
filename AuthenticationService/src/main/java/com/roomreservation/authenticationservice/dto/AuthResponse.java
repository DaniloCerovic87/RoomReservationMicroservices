package com.roomreservation.authenticationservice.dto;

public record AuthResponse(
        String token,
        Long userId,
        String username,
        String email,
        String role
) {
}