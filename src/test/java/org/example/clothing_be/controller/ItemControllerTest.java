package org.example.clothing_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.clothing_be.config.JwtUtils;
import org.example.clothing_be.dto.admin.req.ItemReq;
import org.example.clothing_be.dto.admin.req.ItemUpdateReq;
import org.example.clothing_be.dto.general.res.ItemRes;

import org.example.clothing_be.dto.general.res.PageResponse;
import org.example.clothing_be.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private JwtUtils jwtUtils; // Mock để context có thể load được

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllItems_shouldReturnPageResponse() throws Exception {
        PageResponse<ItemRes> pageResponse = new PageResponse<ItemRes>();
        // Giả lập dữ liệu cho pageResponse nếu cần
        when(itemService.getAllItems(0, 10)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/item")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void createItem_shouldReturnCreated() throws Exception {
        ItemReq req = new ItemReq();
        req.setName("Áo thun");
        req.setPrice(150000.0);
        req.setUrlImg("http://img.jpg");
        req.setInventoryQty(10);

        ItemRes res = new ItemRes();
        res.setId(1);
        res.setName("Áo thun");


        when(itemService.createItem(any(ItemReq.class))).thenReturn(res);

        mockMvc.perform(post("/api/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.result.name").value("Áo thun"));
    }

    @Test
    void updateItem_shouldReturnOk() throws Exception {
        ItemUpdateReq updateReq = new ItemUpdateReq();
        ItemRes res = new ItemRes();
        res.setId(1);

        when(itemService.updateItem(eq(1), any(ItemUpdateReq.class))).thenReturn(res);

        mockMvc.perform(patch("/api/item/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cập nhật sản phẩm thành công"));
    }

    @Test
    void deleteItem_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/item/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xoá sản phẩm thành công!"));
    }

    @Test
    void getSuggestions_shouldReturnEmptyList_whenQueryTooShort() throws Exception {
        mockMvc.perform(get("/api/item/search/suggest")
                        .param("q", "a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isEmpty());
    }

    @Test
    void getSuggestions_shouldReturnList_whenQueryIsValid() throws Exception {
        ItemRes item = new ItemRes();
        item.setId(1);
        item.setName("Quần Jean");

        when(itemService.findTop10Item("quan")).thenReturn(List.of(item));

        mockMvc.perform(get("/api/item/search/suggest")
                        .param("q", "quan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].name").value("Quần Jean"));
    }

    @Test
    void findLowStockItems_shouldReturnList() throws Exception {
        when(itemService.findLowStockItems()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/item/out-of-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lấy danh sách các sản phẩm hết hàng thành công"));
    }

    @Test
    void getItemDetail_shouldReturnDetail() throws Exception {
        ItemRes res = new ItemRes();
        res.setId(1);
        when(itemService.getItemDetail(1)).thenReturn(res);

        mockMvc.perform(get("/api/item/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(1));
    }
}
