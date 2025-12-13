package com.roomreservation.employeeservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEmployeeRequest(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        String personalId,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String title,

        @NotBlank
        String academicRank,

        @NotNull
        Long departmentId
) {
}