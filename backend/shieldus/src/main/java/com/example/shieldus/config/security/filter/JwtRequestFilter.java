package com.example.shieldus.config.security.filter;

import com.example.shieldus.config.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtRequestFilter  extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtRequestFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = null;

        // 1. Authorization 헤더에서 JWT 추출 시도 (프론트엔드에서 명시적으로 헤더에 추가하는 경우)
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("JWT found in Authorization header for URI: " + request.getRequestURI());
        }
        // 2. JWT가 헤더에 없으면 HttpOnly 쿠키에서 추출 시도 (권장 방식)
        if (jwt == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("Authorization".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    if (jwt != null && jwt.startsWith("Bearer ")) {
                        jwt = jwt.substring(7);
                    }
                    break;
                }
            }
        }
        if (jwt != null) {
            try {
                if (jwtTokenProvider.validateToken(jwt)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("User authenticated: " + authentication.getName() + " for URI: " + request.getRequestURI());
                }
            } catch (Exception e) {
                System.err.println("JWT authentication failed for URI: " + request.getRequestURI() + ": " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);

    }
}