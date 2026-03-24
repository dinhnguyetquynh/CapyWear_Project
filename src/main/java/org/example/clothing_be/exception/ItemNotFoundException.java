package org.example.clothing_be.exception;

public class ItemNotFoundException extends NotFoundException{

    public ItemNotFoundException() {
        super("ITEM_NOT_FOUND", "Không tìm thấy item");
    }
}
