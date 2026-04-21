package org.example.clothing_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.clothing_be.config.JwtUtils;
import org.example.clothing_be.dto.admin.req.RolePermissionReq;
import org.example.clothing_be.dto.admin.res.RolePermissionRes;
import org.example.clothing_be.service.RolePermissionService;
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

@WebMvcTest(RolePermissionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RolePermissionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolePermissionService rolePermissionService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addNewRolePermission_shouldReturnCreated() throws Exception {
        RolePermissionReq req = new RolePermissionReq();
        req.setRoleId(1);
        req.setPermissionId(5L);

        RolePermissionRes res = new RolePermissionRes();
        res.setRole("ROLE_ADMIN");
        res.setPermission("UPDATE_ITEM");

        when(rolePermissionService.addNewRP(any(RolePermissionReq.class))).thenReturn(res);

        mockMvc.perform(post("/api/rp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated()) // HTTP 201
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Them permission cho role thanh cong"))
                .andExpect(jsonPath("$.result.role").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.result.permission").value("UPDATE_ITEM"));
    }
}
