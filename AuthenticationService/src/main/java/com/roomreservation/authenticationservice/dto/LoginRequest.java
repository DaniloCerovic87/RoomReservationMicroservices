package com.roomreservation.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank
        String username,

        @NotBlank
//        @Size(min = 8, max = 64)
        String password

) {}