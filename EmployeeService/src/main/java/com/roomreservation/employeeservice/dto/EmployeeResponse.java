package com.roomreservation.employeeservice.dto;

import com.roomreservation.employeeservice.model.Employee;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String personalId,
        String email,
        String title,
        String department
) {
    public static EmployeeResponse fromEntity(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getPersonalId(),
                employee.getEmail(),
                employee.getTitle(),
                employee.getDepartment()
        );
    }
}
