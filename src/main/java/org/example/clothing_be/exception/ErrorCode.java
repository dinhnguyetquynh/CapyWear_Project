package org.example.clothing_be.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 404 NOT FOUND
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "Người dùng không tồn tại"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "Tài nguyên không tồn tại"),

    // 400 BAD REQUEST
    EMAIL_IS_USED(HttpStatus.BAD_REQUEST, "EMAIL_IS_USED", "Email này đã được sử dụng"),
    RESOURCE_NOT_CORRECT(HttpStatus.BAD_REQUEST, "RESOURCE_NOT_CORRECT", "Dữ liệu không chính xác"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Dữ liệu đầu vào không hợp lệ"),
    EMAIL_PASS_NOT_CORRECT(HttpStatus.BAD_REQUEST,"EMAIL_PASS_NOT_CORRECT","Email hoặc mật khẩu không chính xác!"),

    // 500 INTERNAL SERVER ERROR
    UNCATEGORIZED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "UNCATEGORIZED_EXCEPTION", "Lỗi hệ thống không xác định");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
