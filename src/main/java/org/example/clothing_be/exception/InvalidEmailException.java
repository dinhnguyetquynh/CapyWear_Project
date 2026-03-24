package org.example.clothing_be.exception;

public class InvalidEmailException extends BadRequestException{

    public InvalidEmailException() {
        super("INVALID_EMAIL_PASSWORD", "Email hoặc password không chính xác!");
    }
}
