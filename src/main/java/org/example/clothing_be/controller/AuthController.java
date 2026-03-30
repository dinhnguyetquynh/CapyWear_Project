package org.example.clothing_be.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.auth.AuthResponse;
import org.example.clothing_be.dto.users.request.AccountCreateReq;
import org.example.clothing_be.dto.users.request.LoginReq;
import org.example.clothing_be.dto.users.respone.UserRes;
import org.example.clothing_be.service.AuthenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenService authenService;

    @PostMapping("/register")
    public ResponseEntity<UserRes> register(@Valid  @RequestBody AccountCreateReq req) {
        UserRes res = authenService.creatAccount(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(
            @RequestParam String otp,
            @RequestParam Long userId
    ) {
        AuthResponse token = authenService.verifyOtp(otp, userId);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh Token is required");
        }
        AuthResponse authResponse = authenService.refreshToken(refreshToken);
        return ResponseEntity.ok(authResponse);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginReq req) {
        AuthResponse response = authenService.login(req);
        return ResponseEntity.ok(response);
    }
}
