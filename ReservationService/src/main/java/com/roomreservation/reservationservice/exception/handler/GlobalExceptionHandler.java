package com.roomreservation.reservationservice.exception.handler;

import com.roomreservation.reservationservice.exception.ResourceNotFoundException;
import com.roomreservation.reservationservice.exception.ValidationException;
import com.roomreservation.reservationservice.exception.response.ApiError;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest req
    ) {
        List<String> errorCodes = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(msg -> msg != null && !msg.isBlank())
                .distinct()
                .toList();

        String url = req.getRequestURL().toString();
        String qs = req.getQueryString();
        String fullUrl = (qs == null || qs.isBlank()) ? url : (url + "?" + qs);

        log.warn(
                "Bean validation failed | {} {} | url={} | codes={}",
                req.getMethod(),
                req.getRequestURI(),
                fullUrl,
                errorCodes,
                ex
        );

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code("VALIDATION_FAILED")
                .errorCodes(errorCodes)
                .build();

        return buildResponseEntity(apiError);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        String url = req.getRequestURL().toString();
        String qs = req.getQueryString();
        String fullUrl = (qs == null || qs.isBlank()) ? url : (url + "?" + qs);

        log.warn("Not found | {} {} | url={} | code={}",
                req.getMethod(),
                req.getRequestURI(),
                fullUrl,
                ex.getErrorCode(),
                ex
        );

        ApiError error = ApiError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .code("NOT_FOUND")
                .errorCodes(List.of(ex.getErrorCode()))
                .build();

        return buildResponseEntity(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidation(ValidationException ex, HttpServletRequest req) {
        String url = req.getRequestURL().toString();
        String qs = req.getQueryString();
        String fullUrl = (qs == null || qs.isBlank()) ? url : (url + "?" + qs);

        log.warn(
                "Validation failed | {} {} | url={} | codes={} | message={}",
                req.getMethod(),
                req.getRequestURI(),
                fullUrl,
                ex.getErrorCodes(),
                ex.getMessage(),
                ex
        );
        ApiError error = ApiError.builder()
                .status(BAD_REQUEST.value())
                .code("VALIDATION_FAILED")
                .errorCodes(ex.getErrorCodes()).build();

        return buildResponseEntity(error);
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
                .code("DOWNSTREAM_SERVICE_ERROR")
                .build();

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllUnhandledExceptions(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception | {} {}", req.getMethod(), req.getRequestURI(), ex);

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code("INTERNAL_SERVER_ERROR")
                .build();

        return buildResponseEntity(apiError);
    }

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

}
