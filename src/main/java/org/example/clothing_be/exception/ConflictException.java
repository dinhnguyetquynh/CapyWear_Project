package org.example.clothing_be.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public abstract class ConflictException extends BaseApiException{
    protected ConflictException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.CONFLICT,null);
    }
}
