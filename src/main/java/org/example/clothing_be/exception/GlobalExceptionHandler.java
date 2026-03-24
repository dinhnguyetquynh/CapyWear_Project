package org.example.clothing_be.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiError> handleAppException(AppException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        return buildResponse(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex,HttpServletRequest request) {
        List<ApiError.DetailError> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ApiError.DetailError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Dữ liệu không hợp lệ", request.getRequestURI(), details);
    }
    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String errorCode, String message, String path, List<ApiError.DetailError> details) {
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .details(details)
                .build();
        return new ResponseEntity<>(error, status);
    }
}
