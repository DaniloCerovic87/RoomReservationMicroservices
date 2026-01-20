package com.roomreservation.reservationservice.exception.handler;

import com.roomreservation.reservationservice.exception.ResourceNotFoundException;
import com.roomreservation.reservationservice.exception.ValidationException;
import com.roomreservation.reservationservice.exception.response.ApiError;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static HttpStatus getHttpStatus(Status.Code code) {
        return switch (code) {
            case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
            case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case UNAVAILABLE, DEADLINE_EXCEEDED -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        log.warn("Validation failed: {}", String.join(" | ", errors));

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .errors(errors)
                .build();

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Resource not found")
                .debugMessage(ex.getMessage())
                .build();

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException ex) {
        log.warn("Malformed request:", ex);

        ApiError.ApiErrorBuilder builder = ApiError.builder()
                .status(BAD_REQUEST.value())
                .message("Malformed request");

        if (!CollectionUtils.isEmpty(ex.getErrors())) {
            builder.errors(ex.getErrors());
        } else {
            builder.debugMessage(ex.getMessage());
        }

        return buildResponseEntity(builder.build());
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ApiError> handleGrpcStatusRuntimeException(StatusRuntimeException ex) {
        Status.Code code = ex.getStatus().getCode();
        HttpStatus httpStatus = getHttpStatus(code);

        String debug = ex.getStatus().getDescription() != null
                ? ex.getStatus().getDescription()
                : ex.getMessage();

        boolean shouldLogError = switch (code) {
            case INTERNAL, UNKNOWN, DATA_LOSS -> true;
            default -> false;
        };

        if (shouldLogError) {
            log.error("Downstream gRPC call failed: status={}, desc={}", code, debug, ex);
        } else {
            log.warn("Downstream gRPC call failed: status={}, desc={}", code, debug);
        }

        ApiError apiError = ApiError.builder()
                .status(httpStatus.value())
                .message("Downstream gRPC call failed")
                .debugMessage(debug)
                .build();

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllUnhandledExceptions(Exception ex) {
        log.error("Unexpected internal error:", ex);

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Unexpected error occurred")
                .debugMessage(ex.getMessage())
                .build();

        return buildResponseEntity(apiError);
    }

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

}
