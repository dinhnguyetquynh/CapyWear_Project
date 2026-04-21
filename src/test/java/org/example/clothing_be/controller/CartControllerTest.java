package org.example.clothing_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.clothing_be.config.JwtUtils;
import org.example.clothing_be.dto.admin.req.CartDetailReq;
import org.example.clothing_be.dto.admin.res.CartDetailRes;
import org.example.clothing_be.repository.CartDetailRepository;
import org.example.clothing_be.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
@WebMvcTest(CartController.class)
//@WithMockUser(authorities = {"ADD_CART", "GET_CART","UPDATE_CART","DELETE_CART"})
@AutoConfigureMockMvc(addFilters = false)
public class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addItem_shouldReturnCreated() throws Exception {
        // Arrange
        CartDetailReq req = new CartDetailReq();
        req.setItemId(1);
        req.setQuantity(2);

        CartDetailRes res = new CartDetailRes();
        res.setId(100);
        res.setQuantity(2);

        when(cartService.addItem(any(CartDetailReq.class))).thenReturn(res);

        // Act & Assert
        mockMvc.perform(post("/api/cart/add-item")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated()) // Kiểm tra HttpStatus 201
                .andExpect(jsonPath("$.code").value(200)) // Kiểm tra code trong ApiRes
                .andExpect(jsonPath("$.result.id").value(100))
                .andExpect(jsonPath("$.message").value("Thêm sản phẩm vào giỏ hàng thành công"));
    }

    @Test
    void getCartDetailsByUser_shouldReturnList() throws Exception {
        // Arrange
        CartDetailRes item = new CartDetailRes();
        item.setId(1);
        when(cartService.getAllByUser(0, 10)).thenReturn(List.of(item));

        // Act & Assert
        mockMvc.perform(get("/api/cart/detail")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result[0].id").value(1));
    }

    @Test
    void updateCartDetail_shouldReturnUpdated() throws Exception {
        // Arrange
        CartDetailRes res = new CartDetailRes();
        res.setId(1);
        res.setQuantity(5);
        when(cartService.updateCartDetail(eq(1), eq(5))).thenReturn(res);

        // Act & Assert
        mockMvc.perform(patch("/api/cart/update/1")
                        .with(csrf())
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.result.quantity").value(5));
    }

    @Test
    void deleteCartDetail_shouldReturnSuccess() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/cart/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa sản phẩm khỏi giỏ hàng thành công"));

        verify(cartService, times(1)).deleteCartDetail(1);
    }
}
