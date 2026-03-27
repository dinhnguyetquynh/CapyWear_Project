package org.example.clothing_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.admin.res.OrderPendingRes;
import org.example.clothing_be.dto.general.res.ApiRes;
import org.example.clothing_be.dto.users.request.OrderRequest;
import org.example.clothing_be.dto.users.respone.OrderResponse;
import org.example.clothing_be.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{userId}")
    public ResponseEntity<ApiRes<List<OrderResponse>>> getOrdersHistory(@PathVariable Long userId){
        List<OrderResponse> responseList = orderService.getOrdersHistory(userId);
        ApiRes<List<OrderResponse>> res = ApiRes.<List<OrderResponse>>builder()
                .code(200)
                .message("Lấy danh sách lịch sử đơn hàng thành công")
                .result(responseList)
                .build();
        return ResponseEntity.ok(res);
    }

    //Function for admin
    @GetMapping("/pending")
    public ResponseEntity<ApiRes<List<OrderPendingRes>>> getOrdersPending(){
        List<OrderPendingRes> responseList = orderService.getOrdersPending();
        ApiRes<List<OrderPendingRes>> res = ApiRes.<List<OrderPendingRes>>builder()
                .code(200)
                .message("Lấy danh sách đơn hàng đang xử lý thành công")
                .result(responseList)
                .build();
        return ResponseEntity.ok(res);
    }

    //function for admin
    @PatchMapping("/{orderId}")
    public ResponseEntity<ApiRes<OrderPendingRes>> changeStatusOrder(@PathVariable Integer orderId){
        OrderPendingRes order = orderService.changeStatusOrder(orderId);
        ApiRes<OrderPendingRes>res = ApiRes.<OrderPendingRes>builder()
                .code(201)
                .message("Thay đổi trạng thái đơn hàng thành công ")
                .result(order)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}