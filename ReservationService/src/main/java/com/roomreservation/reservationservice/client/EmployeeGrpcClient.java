package com.roomreservation.reservationservice.client;

import com.roomreservation.contracts.employee.grpc.EmployeeGrpcServiceGrpc;
import com.roomreservation.contracts.employee.grpc.GetEmployeeSummaryRequest;
import com.roomreservation.contracts.employee.grpc.GetEmployeeSummaryResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class EmployeeGrpcClient {

    @GrpcClient("employeeService")
    private EmployeeGrpcServiceGrpc.EmployeeGrpcServiceBlockingStub stub;

    public GetEmployeeSummaryResponse getEmployeeSummary(Long employeeId) {
        var req = GetEmployeeSummaryRequest.newBuilder()
                .setEmployeeId(employeeId)
                .build();

        return stub.getEmployeeSummary(req);
    }
}
