package com.roomreservation.employeeservice.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateEmployeeRequest(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        String title,

        @NotBlank
        String department

) {}