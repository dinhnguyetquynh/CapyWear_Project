package org.example.clothing_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.admin.req.CartDetailReq;
import org.example.clothing_be.dto.admin.res.CartDetailRes;
import org.example.clothing_be.dto.general.res.ApiRes;
import org.example.clothing_be.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    @PostMapping("/add-item/{userId}")
    public ResponseEntity<ApiRes<CartDetailRes>> addItem(@PathVariable Long userId, @RequestBody CartDetailReq req){
        CartDetailRes res = cartService.addItem(userId,req);
        ApiRes<CartDetailRes> respone = ApiRes.<CartDetailRes>builder()
                .code(1000)
                .message("Thêm sản phẩm vào giỏ hàng thành công")
                .result(res)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(respone);

    }
}
