package org.example.clothing_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.general.res.ApiRes;
import org.example.clothing_be.dto.users.request.OrderRequest;
import org.example.clothing_be.dto.users.respone.OrderResponse;
import org.example.clothing_be.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/user/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping
    public ResponseEntity<ApiRes<OrderResponse>> createOrder(@RequestBody OrderRequest request){
        OrderResponse orderResponse = orderService.createOrder(request);
        ApiRes<OrderResponse> res = ApiRes.<OrderResponse>builder()
                .code(201)
                .message("Tạo order thành công")
                .result(orderResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}