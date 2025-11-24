package com.roomreservation.authenticationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteUserRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String username,

        @NotNull
        Long employeeId

) {
}