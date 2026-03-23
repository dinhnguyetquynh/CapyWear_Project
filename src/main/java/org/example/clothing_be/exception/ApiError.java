package org.example.clothing_be.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ApiError {
    private LocalDateTime timestamp;
    private int status;       // VD: 404
    private String errorCode; // VD: "USER_NOT_FOUND" (Dùng để FE check logic hoặc đa ngôn ngữ)
    private String message;   // Tin nhắn dễ hiểu cho dev/user
    private String path;      // API path gây lỗi
    private List<DetailError> details;

    @Data
    @AllArgsConstructor
    public static class DetailError {
        private String field;   // Tên trường bị lỗi (vd: email)
        private String message; // Câu thông báo lỗi (vd: must be a well-formed email address)
    }
}
