package com.roomreservation.authenticationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InviteUserRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String username;

    @NotNull
    private Long employeeId;

}
