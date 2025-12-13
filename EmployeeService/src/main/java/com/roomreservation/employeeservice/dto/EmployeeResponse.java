package com.roomreservation.employeeservice.dto;

import com.roomreservation.employeeservice.model.Employee;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String personalId,
        String email,
        String title,
        String academicRank,
        DepartmentResponse department
) {
    public static EmployeeResponse fromEntity(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getPersonalId(),
                employee.getEmail(),
                employee.getTitle(),
                employee.getAcademicRank().getValue(),
                new DepartmentResponse(employee.getDepartment().getId(), employee.getDepartment().getName())
        );
    }
}
