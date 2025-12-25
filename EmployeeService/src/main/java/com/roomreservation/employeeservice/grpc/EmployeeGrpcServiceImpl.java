package com.roomreservation.employeeservice.grpc;

import com.roomreservation.contracts.employee.grpc.EmployeeGrpcServiceGrpc;
import com.roomreservation.contracts.employee.grpc.GetEmployeeSummaryRequest;
import com.roomreservation.contracts.employee.grpc.GetEmployeeSummaryResponse;
import com.roomreservation.employeeservice.dto.EmployeeResponse;
import com.roomreservation.employeeservice.exception.ResourceNotFoundException;
import com.roomreservation.employeeservice.service.EmployeeService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class EmployeeGrpcServiceImpl extends EmployeeGrpcServiceGrpc.EmployeeGrpcServiceImplBase {

    private final EmployeeService employeeService;

    @Override
    public void getEmployeeSummary(GetEmployeeSummaryRequest request,
                                   StreamObserver<GetEmployeeSummaryResponse> responseObserver) {
        GetEmployeeSummaryResponse resp;
        try {
            EmployeeResponse employee = employeeService.getEmployee(request.getEmployeeId());

            String fullName = employee.firstName() + " " + employee.lastName();

            resp = GetEmployeeSummaryResponse.newBuilder()
                    .setExists(true)
                    .setEmployeeId(employee.id())
                    .setFullName(fullName)
                    .build();
        } catch (ResourceNotFoundException ex) {
            resp = GetEmployeeSummaryResponse.newBuilder()
                    .setExists(false)
                    .build();
        }
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

}
