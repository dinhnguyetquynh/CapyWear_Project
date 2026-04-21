package org.example.clothing_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.clothing_be.config.JwtUtils;
import org.example.clothing_be.dto.auth.AuthResponse;
import org.example.clothing_be.dto.users.request.AccountCreateReq;
import org.example.clothing_be.dto.users.request.LoginReq;
import org.example.clothing_be.dto.users.respone.UserRes;
import org.example.clothing_be.service.AuthenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenService authenService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_shouldReturnUserRes() throws Exception {
        AccountCreateReq req = new AccountCreateReq();
        req.setEmail("newuser@gmail.com");
        req.setPassword("123456");

        UserRes res = new UserRes();
        res.setEmail("newuser@gmail.com");

        when(authenService.creatAccount(any(AccountCreateReq.class))).thenReturn(res);

        mockMvc.perform(post("/api/public/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@gmail.com"));
    }

    @Test
    void verifyOtp_shouldReturnAuthResponse() throws Exception {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken("fake-jwt-token");

        when(authenService.verifyOtp(anyString(), anyLong())).thenReturn(authResponse);

        mockMvc.perform(post("/api/public/verify-otp")
                        .param("otp", "123456")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("fake-jwt-token"));
    }

    @Test
    void login_shouldReturnAuthResponse() throws Exception {
        LoginReq req = new LoginReq();
        req.setEmail("user@gmail.com");
        req.setPassword("password");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken("access-token");

        when(authenService.login(any(LoginReq.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    void refresh_shouldReturnNewToken() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("refreshToken", "old-refresh-token");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken("new-access-token");

        when(authenService.refreshToken("old-refresh-token")).thenReturn(authResponse);

        mockMvc.perform(post("/api/public/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"));
    }

    @Test
    void refresh_shouldReturnBadRequest_whenTokenMissing() throws Exception {
        Map<String, String> request = new HashMap<>(); // Body trống

        mockMvc.perform(post("/api/public/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Refresh Token is required"));
    }

    @Test
    void socialLogin_shouldReturnAuthResponse() throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("token", "google-token");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken("social-access-token");

        when(authenService.socialLogin(anyMap())).thenReturn(authResponse);

        mockMvc.perform(post("/api/public/social-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("social-access-token"));
    }
}
