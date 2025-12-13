package com.roomreservation.employeeservice.dto;

public record UpdateEmployeeRequest(

        String firstName,

        String lastName,

        String title,

        String academicRank,

        Long departmentId
) {
}