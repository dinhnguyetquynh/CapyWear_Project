package org.example.clothing_be.exception;

public class CartDetailNotFound extends NotFoundException{

    public CartDetailNotFound(String message) {
        super("CART_DETAIL_NOT_FOUND",message);
    }
}
