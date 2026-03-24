package org.example.clothing_be.exception;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super("USER_NOT_FOUND", "Không tìm thấy người dùng ");
    }
}
