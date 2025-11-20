package com.roomreservation.employeeservice.service;

import com.roomreservation.employeeservice.dto.CreateEmployeeRequest;
import com.roomreservation.employeeservice.dto.EmployeeResponse;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse createEmployee(CreateEmployeeRequest request);

    EmployeeResponse getEmployee(Long id);

    List<EmployeeResponse> getAllEmployees();

}
