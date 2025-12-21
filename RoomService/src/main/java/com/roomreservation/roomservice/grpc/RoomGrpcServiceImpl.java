package com.roomreservation.roomservice.grpc;

import com.roomreservation.contracts.room.grpc.GetRoomSummariesRequest;
import com.roomreservation.contracts.room.grpc.GetRoomSummariesResponse;
import com.roomreservation.contracts.room.grpc.RoomGrpcServiceGrpc;
import com.roomreservation.contracts.room.grpc.RoomSummary;
import com.roomreservation.roomservice.service.RoomService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class RoomGrpcServiceImpl extends RoomGrpcServiceGrpc.RoomGrpcServiceImplBase {

    private final RoomService roomService;

    @Override
    public void getRoomSummaries(GetRoomSummariesRequest request,
                                 StreamObserver<GetRoomSummariesResponse> responseObserver) {

        List<RoomSummary> roomSummaries = roomService.getRoomSummaries(request.getRoomIdsList());

        responseObserver.onNext(
                GetRoomSummariesResponse.newBuilder().addAllRooms(roomSummaries).build()
        );
        responseObserver.onCompleted();
    }

}
