package org.example.clothing_be.service;

import org.example.clothing_be.dto.auth.AuthResponse;
import org.example.clothing_be.dto.users.request.AccountCreateReq;
import org.example.clothing_be.dto.users.request.LoginReq;
import org.example.clothing_be.dto.users.respone.UserRes;

import java.util.Map;

public interface AuthenService {
    String generateOtp();
    void sendOtpEmail(String toEmail, String otpCode);
    AuthResponse verifyOtp(String inputOtp, Long userId);
    UserRes creatAccount(AccountCreateReq req);
    AuthResponse refreshToken(String oldRefreshToken);
    AuthResponse login(LoginReq req);
    AuthResponse socialLogin(Map<String, String> data) throws Exception;
}