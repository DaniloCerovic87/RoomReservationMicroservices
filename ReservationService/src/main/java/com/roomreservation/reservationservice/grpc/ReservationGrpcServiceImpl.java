package com.roomreservation.reservationservice.grpc;

import com.roomreservation.contracts.reservation.grpc.HasActiveReservationsForRoomRequest;
import com.roomreservation.contracts.reservation.grpc.HasActiveReservationsForRoomResponse;
import com.roomreservation.contracts.reservation.grpc.ReservationGrpcServiceGrpc;
import com.roomreservation.reservationservice.service.ReservationService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class ReservationGrpcServiceImpl extends ReservationGrpcServiceGrpc.ReservationGrpcServiceImplBase {

    private final ReservationService reservationService;

    @Override
    public void hasActiveReservationsForRoom(HasActiveReservationsForRoomRequest request,
                                             StreamObserver<HasActiveReservationsForRoomResponse> responseObserver) {
        boolean hasActive = reservationService.hasActiveReservationForRoom(request.getRoomId());

        responseObserver.onNext(
                HasActiveReservationsForRoomResponse.newBuilder()
                        .setHasActive(hasActive)
                        .build()
        );
        responseObserver.onCompleted();
    }

}
