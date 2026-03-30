package org.example.clothing_be.service;

import org.example.clothing_be.dto.admin.req.CartDetailReq;
import org.example.clothing_be.dto.admin.res.CartDetailRes;

import java.util.List;

public interface CartService {
    CartDetailRes addItem(CartDetailReq req);
    List<CartDetailRes> getAllByUser(int page, int size);
    CartDetailRes updateCartDetail(Integer id,Integer quantity);
    void deleteCartDetail(Integer cartDetailId);
}
