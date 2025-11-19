package com.roomreservation.employeeservice.service;

import com.roomreservation.employeeservice.client.AuthServiceClient;
import com.roomreservation.employeeservice.dto.CreateEmployeeRequest;
import com.roomreservation.employeeservice.dto.EmployeeResponse;
import com.roomreservation.employeeservice.dto.InviteUserRequest;
import com.roomreservation.employeeservice.exception.EmailAlreadyExistsException;
import com.roomreservation.employeeservice.exception.PersonalIdAlreadyExistsException;
import com.roomreservation.employeeservice.model.Employee;
import com.roomreservation.employeeservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuthServiceClient authServiceClient;

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {

        if (employeeRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Employee with given email already exists");
        }

        if (employeeRepository.existsByPersonalId(request.personalId())) {
            throw new PersonalIdAlreadyExistsException("Employee with given personalId already exists");
        }

        Employee employee = Employee.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .personalId(request.personalId())
                .email(request.email())
                .title(request.title())
                .department(request.department())
                .build();

        employeeRepository.save(employee);

        String username = generateUsername(employee);

        InviteUserRequest inviteRequest =
                new InviteUserRequest(employee.getEmail(), username, employee.getId());

        authServiceClient.inviteUser(inviteRequest);

        return EmployeeResponse.fromEntity(employee);
    }


    private String generateUsername(Employee employee) {
        return (employee.getFirstName() + "." + employee.getLastName()).toLowerCase();
    }

}
