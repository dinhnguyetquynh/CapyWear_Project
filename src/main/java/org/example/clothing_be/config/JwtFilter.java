package org.example.clothing_be.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtils.validateToken(token) && SecurityContextHolder.getContext().getAuthentication() == null) {

                    String username = jwtUtils.extractUsername(token);
                    List<String> authoritiesList = jwtUtils.extractAuthorities(token);

                    // QUAN TRỌNG: Không cộng chuỗi "ROLE_" nữa!
                    List<SimpleGrantedAuthority> authorities = authoritiesList.stream()
                            .map(authority -> new SimpleGrantedAuthority(authority))
                            // Có thể viết gọn là: .map(SimpleGrantedAuthority::new)
                            .toList();

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            log.error("JWT processing failed: {}", e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getRequestURI();
//        // Bỏ qua không kiểm tra token với các API bắt đầu bằng /api/public/
//        return path.startsWith("/api/public/") || path.startsWith("/api/item") || path.startsWith("/h2-console")||path.startsWith("/error");
//    }
}