package org.example.clothing_be.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.admin.req.ItemReq;
import org.example.clothing_be.dto.general.res.ApiRes;
import org.example.clothing_be.dto.general.res.ItemRes;
import org.example.clothing_be.entity.Item;
import org.example.clothing_be.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    @GetMapping
    public ResponseEntity<Page<ItemRes>> getAllItems(@RequestParam(name = "page", defaultValue = "0") int page,
                                                     @RequestParam(name = "size", defaultValue = "10") int size){
        Page<ItemRes> result = itemService.getAllItems(page, size);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ApiRes<ItemRes>> createItem(@Valid @RequestBody ItemReq req){
        ItemRes itemRes = itemService.createItem(req);

        ApiRes<ItemRes> res = ApiRes.<ItemRes>builder()
                .code(201)
                .message("Thêm sản phẩm mới thành công !")
                .result(itemRes)
                .build();

        // Trả về với HTTP Status 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}
