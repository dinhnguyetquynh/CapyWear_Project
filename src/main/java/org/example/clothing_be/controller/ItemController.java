package org.example.clothing_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.general.res.ItemRes;
import org.example.clothing_be.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
