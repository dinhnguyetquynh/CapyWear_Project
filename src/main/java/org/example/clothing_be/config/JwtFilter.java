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

        System.out.println("AUTH HEADER: {}"+ authHeader);

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
//                log.info("TOKEN: {}", token);
                System.out.println("TOKEN:"+token);
                boolean isValid = jwtUtils.validateToken(token);

//                log.info("TOKEN VALID: {}", isValid);
                System.out.println("TOKEN VALID:"+isValid);

                if (isValid && SecurityContextHolder.getContext().getAuthentication() == null) {

                    String username = jwtUtils.extractUsername(token);

                    log.info("USERNAME: {}", username);
                    List<String> authoritiesList = jwtUtils.extractAuthorities(token);
                    log.info("AUTHORITIES: {}", authoritiesList);

                    List<SimpleGrantedAuthority> authorities = authoritiesList.stream()
                            .map(authority -> new SimpleGrantedAuthority(authority))
                            .toList();

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("AUTHENTICATION SUCCESS");
                }
            }
        } catch (Exception e) {
            log.error("JWT ERROR", e);
            SecurityContextHolder.clearContext();
            log.error("JWT processing failed: {}", e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }
}