package com.roomreservation.employeeservice.service.impl;

import com.roomreservation.employeeservice.client.AuthServiceClient;
import com.roomreservation.employeeservice.dto.CreateEmployeeRequest;
import com.roomreservation.employeeservice.dto.EmployeeResponse;
import com.roomreservation.employeeservice.dto.InviteUserRequest;
import com.roomreservation.employeeservice.exception.EmailAlreadyExistsException;
import com.roomreservation.employeeservice.exception.PersonalIdAlreadyExistsException;
import com.roomreservation.employeeservice.exception.ResourceNotFoundException;
import com.roomreservation.employeeservice.model.Employee;
import com.roomreservation.employeeservice.repository.EmployeeRepository;
import com.roomreservation.employeeservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuthServiceClient authServiceClient;

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {

        if (employeeRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        if (employeeRepository.existsByPersonalId(request.personalId())) {
            throw new PersonalIdAlreadyExistsException(request.personalId());
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

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeResponse::fromEntity)
                .toList();
    }

}
