package com.roomreservation.employeeservice.repository;

import com.roomreservation.employeeservice.model.Employee;

public interface EmployeeRepository extends SoftDeleteRepository<Employee, Long> {

    boolean existsByEmail(String email);

    boolean existsByPersonalId(String personalId);

}