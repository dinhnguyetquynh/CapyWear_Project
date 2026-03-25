package org.example.clothing_be.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public abstract class AccessDeniedException extends BaseApiException{

    protected AccessDeniedException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.FORBIDDEN, null);
    }
}
