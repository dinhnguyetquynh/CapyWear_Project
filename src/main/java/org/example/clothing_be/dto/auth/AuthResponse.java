package org.example.clothing_be.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private int expiresIn;
    private String tokenType = "Bearer";

    public AuthResponse(String accessToken, String refreshToken,int expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn=expiresIn;
    }
}
