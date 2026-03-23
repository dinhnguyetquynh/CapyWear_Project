package org.example.clothing_be.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;
    @Value("${jwt.refreshExpiration}")
    private long REFRESH_EXPIRATION_TIME;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    // Tạo Refresh Token (Mới: Thường không cần chứa Roles để bảo mật và nhẹ payload)
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object rolesObj = claims.get("roles");

        if (rolesObj instanceof List<?>) {
            return ((List<?>) rolesObj).stream()
                    .map(Object::toString)
                    .toList();
        }

        return List.of();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token đã hết hạn: {}", e.getMessage());
//            throw e; // Ném ra để Service hoặc Global Handler xử lý
        } catch (SignatureException e) {
            log.error("Chữ ký Token không hợp lệ: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Token không hợp lệ: {}", e.getMessage());
        }
        return false;
    }
}
