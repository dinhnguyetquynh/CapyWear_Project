package org.example.clothing_be.exception;

public class ItemAlreadyExistsException extends ConflictException{

    public ItemAlreadyExistsException() {
        super("ITEM_ALREADY_EXISTS", "Sản phẩm này đã tồn tại trong hệ thống");
    }
}
