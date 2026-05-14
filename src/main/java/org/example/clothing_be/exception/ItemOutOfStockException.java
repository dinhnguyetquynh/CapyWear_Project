package org.example.clothing_be.exception;

public class ItemOutOfStockException extends NotFoundException{
    public ItemOutOfStockException() {
        super("ITEM_NOT_FOUND", "Sản phẩm tạm hết hàng");
    }
}
