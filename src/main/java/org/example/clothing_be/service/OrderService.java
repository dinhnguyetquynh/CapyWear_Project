package org.example.clothing_be.service;

import org.example.clothing_be.dto.users.request.OrderRequest;
import org.example.clothing_be.dto.users.respone.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
}
