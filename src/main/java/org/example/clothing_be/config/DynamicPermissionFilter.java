package org.example.clothing_be.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.clothing_be.exception.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class DynamicPermissionFilter extends OncePerRequestFilter {
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUrl = request.getRequestURI();
        String requestMethod = request.getMethod();

        if (requestUrl.startsWith("/api/public")) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }
        boolean hasPermission = false;

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String authString = authority.getAuthority();
            if (!authString.contains(":")) {
                continue;
            }

            String[] parts = authString.split(":", 2);
            String allowedMethod = parts[0];     // VD: "GET"
            String allowedResource = parts[1];   // VD: "/api/items/**"

            if (allowedMethod.equalsIgnoreCase(requestMethod) && pathMatcher.match(allowedResource, requestUrl)) {
                hasPermission = true;
                break;
            }
        }

        if (hasPermission) {
            log.info("Cấp quyền truy cập: User '{}' gọi API [{}] {}", authentication.getName(), requestMethod, requestUrl);
            filterChain.doFilter(request, response); // Access api success
        } else {
            log.warn("Từ chối truy cập: User '{}' cố gắng gọi API [{}] {}", authentication.getName(), requestMethod, requestUrl);
            sendErrorResponse(response, HttpStatus.FORBIDDEN, "PERMISSION_DENIED", "Bạn không có quyền thực hiện hành động này", requestUrl);
        }
    }
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String errorCode, String message, String path) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Giả sử bro có class ApiError này (nếu khác tên thì bro tự đổi nhé)
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        response.getWriter().write(mapper.writeValueAsString(error));
    }
}
