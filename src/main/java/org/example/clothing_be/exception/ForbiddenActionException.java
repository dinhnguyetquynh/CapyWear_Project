package org.example.clothing_be.exception;

public class ForbiddenActionException extends AccessDeniedException{
    public ForbiddenActionException() {
        super("FORBIDDEN_ACTION", "Bạn không có quyền thực hiện hành động này");
    }
}
