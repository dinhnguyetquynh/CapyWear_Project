package org.example.clothing_be.service;

import org.example.clothing_be.dto.admin.req.CartDetailReq;
import org.example.clothing_be.dto.admin.res.CartDetailRes;

public interface CartService {
    CartDetailRes addItem(Long userId,CartDetailReq req);
}
