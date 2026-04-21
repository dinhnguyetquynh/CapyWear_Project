package org.example.clothing_be.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.clothing_be.config.JwtUtils;
import org.example.clothing_be.dto.admin.req.PermissionReq;
import org.example.clothing_be.dto.admin.res.PermissionRes;
import org.example.clothing_be.service.PermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PermissionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PermissionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PermissionService permissionService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPermission_shouldReturnCreated() throws Exception {
        PermissionReq req = new PermissionReq();
        req.setName("USER_VIEW");
        req.setDescription("Quyền xem danh sách người dùng");
        req.setAction("GET");
        req.setResource("/api/permission");

        PermissionRes res = new PermissionRes();
        res.setId(1L);
        res.setName("USER_VIEW");
        res.setDescription("Quyền xem danh sách người dùng");

        when(permissionService.addPermission(any(PermissionReq.class))).thenReturn(res);

        mockMvc.perform(post("/api/permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated()) // Kiểm tra HTTP Status 201
                .andExpect(jsonPath("$.code").value(201)) // Kiểm tra code trong ApiRes
                .andExpect(jsonPath("$.message").value("Tạo Permission thành công"))
                .andExpect(jsonPath("$.result.name").value("USER_VIEW"));
    }

    @Test
    void createPermission_shouldReturnBadRequest_whenNameIsEmpty() throws Exception {
        PermissionReq invalidReq = new PermissionReq();
        invalidReq.setName("");

        mockMvc.perform(post("/api/permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest());
    }
}
