package org.example.clothing_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public abstract class BaseApiException extends RuntimeException {
    private final ApiError apiError;
    private final HttpStatus status;
    protected BaseApiException(String errorCode, String message, HttpStatus status, List<ApiError.DetailError> details) {
        super(message);
        // Tự động lấy URI của request hiện tại
        String currentPath = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .build()
                .getPath();
        this.status = status;
        // Tự tạo ApiError ngay khi Exception được khởi tạo
        this.apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .errorCode(errorCode)
                .message(message)
                .path(currentPath)
                .details(details)
                .build();
    }

}
