package com.roomreservation.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompleteRegistrationRequest(

        @NotBlank
        String token,

        @NotBlank
        @Size(min = 8, max = 20)
        String password

) {
}
