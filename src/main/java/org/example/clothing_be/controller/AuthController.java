package org.example.clothing_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.users.request.AccountCreateReq;
import org.example.clothing_be.dto.users.respone.UserRes;
import org.example.clothing_be.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserRes> register(@RequestBody AccountCreateReq req) {
        UserRes res = authService.creatAccount(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestParam String otp,
            @RequestParam Long userId
    ) {
        String token = authService.verifyOtp(otp, userId);
        return ResponseEntity.ok(token);
    }
}
