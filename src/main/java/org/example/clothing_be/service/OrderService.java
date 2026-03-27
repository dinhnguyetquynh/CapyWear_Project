package org.example.clothing_be.service;

import org.example.clothing_be.dto.admin.res.OrderPendingRes;
import org.example.clothing_be.dto.users.request.OrderRequest;
import org.example.clothing_be.dto.users.respone.OrderResponse;
import org.example.clothing_be.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    List<OrderResponse> getOrdersHistory(Long userId);
    List<OrderPendingRes> getOrdersPending();
    OrderPendingRes changeStatusOrder(Integer orderId);
}
