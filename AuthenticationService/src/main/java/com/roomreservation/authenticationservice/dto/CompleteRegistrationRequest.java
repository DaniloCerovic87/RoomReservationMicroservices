package com.roomreservation.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CompleteRegistrationRequest(

        @NotBlank
        String token,

        @NotBlank
        String password

) {}
