package org.example.clothing_be.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
// Đại diện cho nhóm lỗi 400
public abstract class BadRequestException extends BaseApiException {
    protected BadRequestException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.BAD_REQUEST,null);
    }
}
