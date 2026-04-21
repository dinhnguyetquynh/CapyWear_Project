package org.example.clothing_be.controller;

import org.example.clothing_be.config.JwtUtils;
import org.example.clothing_be.dto.users.respone.UserRes;
import org.example.clothing_be.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtils jwtUtils;
    @Test
    void getProfileUser_shouldReturnUserProfile() throws Exception {
        UserRes mockResponse = new UserRes();
        mockResponse.setId(1L);
        mockResponse.setEmail("testuser@gmail.com");


        when(userService.getProfileUser()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Kỳ vọng HTTP 200
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lấy thông tin người dùng thành công"))
                .andExpect(jsonPath("$.result.email").value("testuser@gmail.com"));
    }
}
