package com.roomreservation.reservationservice.client;

import com.roomreservation.contracts.employee.grpc.EmployeeGrpcServiceGrpc;
import com.roomreservation.contracts.employee.grpc.GetEmployeeSummaryRequest;
import com.roomreservation.contracts.employee.grpc.GetEmployeeSummaryResponse;
import com.roomreservation.reservationservice.interceptor.JwtGrpcClientInterceptor;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeGrpcClient {

    @GrpcClient("employeeService")
    private EmployeeGrpcServiceGrpc.EmployeeGrpcServiceBlockingStub stub;

    private final JwtGrpcClientInterceptor jwtGrpcClientInterceptor;

    public GetEmployeeSummaryResponse getEmployeeSummary(Long employeeId) {
        var req = GetEmployeeSummaryRequest.newBuilder()
                .setEmployeeId(employeeId)
                .build();

        var securedStub = stub.withInterceptors(jwtGrpcClientInterceptor);
        return securedStub.getEmployeeSummary(req);
    }
}
