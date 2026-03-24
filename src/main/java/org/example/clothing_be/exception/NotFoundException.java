package org.example.clothing_be.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public abstract class NotFoundException extends BaseApiException {
    protected NotFoundException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.NOT_FOUND, null);
    }
}
