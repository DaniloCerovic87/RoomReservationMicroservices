package com.roomreservation.reservationservice.client;

import com.roomreservation.contracts.employee.grpc.EmployeeGrpcServiceGrpc;
import com.roomreservation.contracts.employee.grpc.ExistsEmployeeRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class EmployeeGrpcClient {

    @GrpcClient("employeeService")
    private EmployeeGrpcServiceGrpc.EmployeeGrpcServiceBlockingStub stub;

    public boolean existsEmployee(Long employeeId) {
        var req = ExistsEmployeeRequest.newBuilder()
                .setEmployeeId(employeeId)
                .build();

        return stub.existsEmployee(req).getExists();
    }
}
