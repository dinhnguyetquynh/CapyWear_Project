package org.example.clothing_be.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.admin.req.ItemReq;
import org.example.clothing_be.dto.admin.req.ItemUpdateReq;
import org.example.clothing_be.dto.general.res.ApiRes;
import org.example.clothing_be.dto.general.res.ItemRes;
import org.example.clothing_be.dto.general.res.ItemSearchRes;
import org.example.clothing_be.dto.general.res.PageResponse;
import org.example.clothing_be.entity.Item;
import org.example.clothing_be.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<PageResponse<ItemRes>> getAllItems(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        PageResponse<ItemRes> result = itemService.getAllItems(page, size);
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
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiRes<ItemRes>> updateItem(@PathVariable Integer id, @RequestBody ItemUpdateReq req) {

        ItemRes result = itemService.updateItem(id, req);

        ApiRes<ItemRes> response = ApiRes.<ItemRes>builder()
                .code(1000)
                .message("Cập nhật sản phẩm thành công")
                .result(result)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiRes<Integer>> deleteItem(@PathVariable Integer id) {
        itemService.deleteItem(id);

        ApiRes<Integer> response = ApiRes.<Integer>builder()
                .code(1000)
                .message("Xoá sản phẩm thành công!")
                .result(id)
                .build();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/search/suggest")
    public ResponseEntity <ApiRes<List<ItemSearchRes>>>  getSuggestions(@RequestParam String q) {
        if (q.length() < 2) {
            return ResponseEntity.ok(ApiRes.<List<ItemSearchRes>>builder()
                    .code(200)
                    .result(Collections.emptyList())
                    .build());
        }

        List<ItemRes> items = itemService.findTop10Item(q);

        // Chuyển đổi từ ItemRes sang ItemSearchRes (chỉ lấy id và name)
        List<ItemSearchRes> suggestions = items.stream()
                .map(item -> new ItemSearchRes(item.getId(), item.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiRes.<List<ItemSearchRes>>builder()
                .code(200)
                .message("Thành công")
                .result(suggestions)
                .build());
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<ApiRes<List<ItemRes>>> findLowStockItems(){
        List<ItemRes> itemResList = itemService.findLowStockItems();

        ApiRes<List<ItemRes>> res = ApiRes.<List<ItemRes>>builder()
                .code(200)
                .message("Lấy danh sách các sản phẩm hết hàng thành công")
                .result(itemResList)
                .build();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ApiRes<ItemRes>> getItemDetail(@PathVariable int itemId){
        ItemRes itemRes = itemService.getItemDetail(itemId);
        return ResponseEntity.ok()
                .body(ApiRes.success(200,itemRes,"Lấy chi tiết sản phẩm thành công"));
    }

}
