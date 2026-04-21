package org.example.clothing_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.clothing_be.config.JwtUtils;
import org.example.clothing_be.dto.admin.res.OrderPendingRes;
import org.example.clothing_be.dto.users.request.OrderRequest;
import org.example.clothing_be.dto.users.respone.OrderResponse;
import org.example.clothing_be.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrder_shouldReturnCreated() throws Exception {
        // Arrange
        OrderRequest request = new OrderRequest();

        OrderResponse response = new OrderResponse();
        response.setOrderId(1);

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Tạo order thành công"))
                .andExpect(jsonPath("$.result.orderId").value(1));
    }

    @Test
    void getOrdersHistory_shouldReturnList() throws Exception {
        // Arrange
        OrderResponse order1 = new OrderResponse();
        order1.setOrderId(1);
        when(orderService.getOrdersHistory()).thenReturn(List.of(order1));

        // Act & Assert
        mockMvc.perform(get("/api/order/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result[0].orderId").value(1));
    }

    @Test
    void getOrdersPending_shouldReturnList() throws Exception {
        // Arrange
        OrderPendingRes pendingOrder = new OrderPendingRes();
        pendingOrder.setOrderId(10);
        when(orderService.getOrdersPending()).thenReturn(List.of(pendingOrder));

        // Act & Assert
        mockMvc.perform(get("/api/order/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lấy danh sách đơn hàng đang xử lý thành công"))
                .andExpect(jsonPath("$.result[0].orderId").value(10));
    }

    @Test
    void changeStatusOrder_shouldReturnCreated() throws Exception {
        // Arrange
        OrderPendingRes updatedOrder = new OrderPendingRes();
        updatedOrder.setOrderId(1);

        when(orderService.changeStatusOrder(1)).thenReturn(updatedOrder);

        // Act & Assert
        mockMvc.perform(patch("/api/order/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Thay đổi trạng thái đơn hàng thành công "));
    }
}
