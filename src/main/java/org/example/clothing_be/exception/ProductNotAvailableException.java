package org.example.clothing_be.exception;

public class ProductNotAvailableException extends BadRequestException{

    public ProductNotAvailableException() {
        super("PRODUCT_DISCONTINUED", "Sản phẩm này đã ngừng kinh doanh");
    }
}
