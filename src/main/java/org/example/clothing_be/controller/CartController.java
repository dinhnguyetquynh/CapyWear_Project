package org.example.clothing_be.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.admin.req.CartDetailReq;
import org.example.clothing_be.dto.admin.res.CartDetailRes;
import org.example.clothing_be.dto.general.res.ApiRes;
import org.example.clothing_be.repository.CartDetailRepository;
import org.example.clothing_be.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final CartDetailRepository cartDetailRepository;

    @PostMapping("/add-item")
    public ResponseEntity<ApiRes<CartDetailRes>> addItem( @Valid @RequestBody CartDetailReq req){
        CartDetailRes res = cartService.addItem(req);
        ApiRes<CartDetailRes> respone = ApiRes.<CartDetailRes>builder()
                .code(200)
                .message("Thêm sản phẩm vào giỏ hàng thành công")
                .result(res)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(respone);
    }

    @GetMapping("/detail")
    public ResponseEntity<ApiRes<List<CartDetailRes>>> getCartDetailsByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<CartDetailRes> cartDetails = cartService.getAllByUser(page, size);
        ApiRes<List<CartDetailRes>> res = ApiRes.<List<CartDetailRes>>builder()
                .code(200)
                .message("Lấy danh sách sản phẩm trong giỏ hàng thành công")
                .result(cartDetails)
                .build();
        return ResponseEntity.ok(res);

    }

    @PatchMapping("/update/{cdId}")
    public ResponseEntity<ApiRes<CartDetailRes>> updateCartDetail(@PathVariable Integer cdId,@RequestParam Integer quantity){
        CartDetailRes detailRes = cartService.updateCartDetail(cdId, quantity);
        ApiRes<CartDetailRes> respone = ApiRes.<CartDetailRes>builder()
                .code(201)
                .message("Cập nhật số lượng sản phẩm thành công")
                .result(detailRes)
                .build();
        return ResponseEntity.ok(respone);
    }
    @DeleteMapping("/{cartDetailId}")
    public ResponseEntity<ApiRes<Void>> deleteCartDetail(@PathVariable Integer cartDetailId) {
        cartService.deleteCartDetail(cartDetailId);

        ApiRes<Void> res = ApiRes.<Void>builder()
                .code(200)
                .message("Xóa sản phẩm khỏi giỏ hàng thành công")
                .build();

        return ResponseEntity.ok(res);
    }
}
