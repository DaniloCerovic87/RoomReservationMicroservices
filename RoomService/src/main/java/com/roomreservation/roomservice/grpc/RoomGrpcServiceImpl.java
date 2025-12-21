package com.roomreservation.roomservice.grpc;

import com.roomreservation.contracts.room.grpc.ExistsAllRoomsRequest;
import com.roomreservation.contracts.room.grpc.ExistsAllRoomsResponse;
import com.roomreservation.contracts.room.grpc.RoomGrpcServiceGrpc;
import com.roomreservation.roomservice.service.RoomService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class RoomGrpcServiceImpl extends RoomGrpcServiceGrpc.RoomGrpcServiceImplBase {

    private final RoomService roomService;

    @Override
    public void existsAllRooms(ExistsAllRoomsRequest request,
                               StreamObserver<ExistsAllRoomsResponse> responseObserver) {

        boolean exists = roomService.existsAllRooms(request.getRoomIdsList());

        responseObserver.onNext(
                ExistsAllRoomsResponse.newBuilder().setExists(exists).build()
        );
        responseObserver.onCompleted();
    }

}
