package com.roomreservation.employeeservice.grpc;

import com.roomreservation.contracts.employee.grpc.EmployeeGrpcServiceGrpc;
import com.roomreservation.contracts.employee.grpc.ExistsEmployeeRequest;
import com.roomreservation.contracts.employee.grpc.ExistsEmployeeResponse;
import com.roomreservation.employeeservice.service.EmployeeService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class EmployeeGrpcServiceImpl extends EmployeeGrpcServiceGrpc.EmployeeGrpcServiceImplBase {

    private final EmployeeService employeeService;

    @Override
    public void existsEmployee(ExistsEmployeeRequest request,
                               StreamObserver<ExistsEmployeeResponse> responseObserver) {

        boolean exists = employeeService.existsActiveEmployee(request.getEmployeeId());

        responseObserver.onNext(
                ExistsEmployeeResponse.newBuilder().setExists(exists).build()
        );
        responseObserver.onCompleted();
    }

}
