package com.roomreservation.employeeservice.repository;

import com.roomreservation.employeeservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    boolean existsByPersonalId(String personalId);

}