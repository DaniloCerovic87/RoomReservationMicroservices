package com.roomreservation.employeeservice.exception.handler;

import com.roomreservation.employeeservice.exception.EmailAlreadyExistsException;
import com.roomreservation.employeeservice.exception.PersonalIdAlreadyExistsException;
import com.roomreservation.employeeservice.exception.ResourceNotFoundException;
import com.roomreservation.employeeservice.exception.client.AuthServiceException;
import com.roomreservation.employeeservice.exception.client.AuthServiceUnavailableException;
import com.roomreservation.employeeservice.exception.response.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        log.warn("Email already exists: {}", ex.getMessage());

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Invalid request data")
                .debugMessage(ex.getMessage())
                .build();

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(PersonalIdAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUsernameAlreadyExists(PersonalIdAlreadyExistsException ex) {
        log.warn("Username already exists: {}", ex.getMessage());

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Invalid request data")
                .debugMessage(ex.getMessage())
                .build();

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {
        String joinedMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((m1, m2) -> m1 + "; " + m2)
                .orElse("Validation error");

        log.warn("Validation failed: {}", joinedMessages);

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .debugMessage(joinedMessages)
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

    @ExceptionHandler(AuthServiceException.class)
    public ResponseEntity<ApiError> handleAuthServiceException(AuthServiceException ex) {
        HttpStatus backendStatus = ex.getBackendStatus();
        HttpStatus clientStatus;
        log.error("Error while calling AuthService: status={}, body={}",
                backendStatus, ex.getBackendBody(), ex);

        if (backendStatus.is4xxClientError()) {
            clientStatus = HttpStatus.BAD_REQUEST;
        } else if (backendStatus.is5xxServerError()) {
            clientStatus = HttpStatus.BAD_GATEWAY;
        } else {
            clientStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ApiError apiError = ApiError.builder()
                .status(clientStatus.value())
                .message("Error while communicating with authentication service")
                .debugMessage("AuthService status: %s, body: %s"
                        .formatted(backendStatus, ex.getBackendBody()))
                .build();

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(AuthServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleAuthServiceUnavailable(AuthServiceUnavailableException ex) {
        log.error("AuthService unreachable: {}", ex.getMessage(), ex);

        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;

        ApiError apiError = ApiError.builder()
                .status(status.value())
                .message("Authentication service is currently unavailable")
                .debugMessage(ex.getMessage())
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
