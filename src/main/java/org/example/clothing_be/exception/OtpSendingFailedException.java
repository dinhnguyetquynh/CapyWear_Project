package org.example.clothing_be.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class OtpSendingFailedException extends RuntimeException {
    public OtpSendingFailedException(String message) {
        super(message);
    }
}
