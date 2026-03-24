package org.example.clothing_be.exception;

public class EmailAlreadyExistsException extends ConflictException{
    public EmailAlreadyExistsException(String email) {
        super("EMAIL_ALREADY_EXISTS", "Email " + email + " đã được sử dụng!");
    }
}
