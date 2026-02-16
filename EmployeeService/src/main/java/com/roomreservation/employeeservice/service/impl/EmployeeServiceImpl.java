package com.roomreservation.employeeservice.service.impl;

import com.roomreservation.employeeservice.client.AuthServiceClient;
import com.roomreservation.employeeservice.dto.CreateEmployeeRequest;
import com.roomreservation.employeeservice.dto.EmployeeResponse;
import com.roomreservation.employeeservice.dto.InviteUserRequest;
import com.roomreservation.employeeservice.dto.UpdateEmployeeRequest;
import com.roomreservation.employeeservice.enums.AcademicRank;
import com.roomreservation.employeeservice.exception.EmailAlreadyExistsException;
import com.roomreservation.employeeservice.exception.PersonalIdAlreadyExistsException;
import com.roomreservation.employeeservice.exception.ResourceNotFoundException;
import com.roomreservation.employeeservice.model.Department;
import com.roomreservation.employeeservice.model.Employee;
import com.roomreservation.employeeservice.repository.DepartmentRepository;
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
    private final DepartmentRepository departmentRepository;
    private final AuthServiceClient authServiceClient;

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {

        if (employeeRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        if (employeeRepository.existsByPersonalId(request.personalId())) {
            throw new PersonalIdAlreadyExistsException(request.personalId());
        }

        Department department = departmentRepository.findByIdAndDeletedFalse(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId()));

        Employee employee = Employee.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .personalId(request.personalId())
                .email(request.email())
                .academicRank(AcademicRank.fromValue(request.academicRank()))
                .title(request.title())
                .department(department)
                .build();

        employeeRepository.save(employee);

        InviteUserRequest inviteRequest =
                new InviteUserRequest(employee.getEmail(), request.username(), employee.getId());

        authServiceClient.inviteUser(inviteRequest);

        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));

        if (employee.isDeleted()) {
            throw new ResourceNotFoundException("Employee", id);
        }

        if (request.departmentId() != null) {
            Department department = departmentRepository.findByIdAndDeletedFalse(request.departmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId()));
            employee.setDepartment(department);
        }

        if (request.firstName() != null) {
            employee.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            employee.setLastName(request.lastName());
        }
        if (request.academicRank() != null) {
            employee.setAcademicRank(AcademicRank.fromValue(request.academicRank()));
        }
        if (request.title() != null) {
            employee.setTitle(request.title());
        }

        employeeRepository.save(employee);

        return EmployeeResponse.fromEntity(employee);
    }


    @Transactional(readOnly = true)
    public EmployeeResponse getEmployee(Long id) {
        Employee employee = employeeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAllByDeletedFalse()
                .stream()
                .map(EmployeeResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        authServiceClient.disableUserByEmployeeId(employee.getId());
        employeeRepository.delete(employee);
    }

}
